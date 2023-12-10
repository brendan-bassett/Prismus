/*
  ==============================================================================

    MidiProcessor.h
    Created: 16 Nov 2023 7:53:02pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>
#include <iostream>

#include "NoteStructure.h"

using namespace std;

//==============================================================================

class MidiComponent : public juce::Component,
    private juce::MidiInputCallback,
    private juce::MidiKeyboardStateListener
{

public:
    //==============================================================================

    MidiComponent()
        : keyboardComponent(keyboardState, juce::MidiKeyboardComponent::horizontalKeyboard),
        startTime(juce::Time::getMillisecondCounterHiRes() * 0.001)
    {

        setOpaque(true);
        midiInputListLabel.setText("MIDI Input:", juce::dontSendNotification);
        midiInputListLabel.attachToComponent(&midiInputList, true);
        addAndMakeVisible(midiInputListLabel);

        midiInputList.setTextWhenNoChoicesAvailable("No MIDI Inputs Enabled");
        auto midiInputs = juce::MidiInput::getAvailableDevices();

        juce::StringArray midiInputNames;

        for (auto input : midiInputs)
            midiInputNames.add(input.name);

        midiInputList.addItemList(midiInputNames, 1);
        midiInputList.onChange = [this] { setMidiInput(midiInputList.getSelectedItemIndex()); };
        addAndMakeVisible(midiInputList);

        // Find the first enabled device and use that by default.
        for (auto input : midiInputs)
        {
            if (deviceManager.isMidiInputDeviceEnabled(input.identifier))
            {
                setMidiInput(midiInputs.indexOf(input));
                break;
            }
        }

        // If no enabled devices were found just use the first one in the list.
        if (midiInputList.getSelectedId() == 0)
            setMidiInput(0);

        keyboardState.addListener(this);
        addAndMakeVisible(keyboardComponent);

        midiMessagesBox.setMultiLine(true);
        midiMessagesBox.setReturnKeyStartsNewLine(true);
        midiMessagesBox.setReadOnly(true);
        midiMessagesBox.setScrollbarsShown(true);
        midiMessagesBox.setCaretVisible(false);
        midiMessagesBox.setPopupMenuEnabled(true);
        midiMessagesBox.setColour(juce::TextEditor::backgroundColourId, juce::Colour(0x32ffffff));
        midiMessagesBox.setColour(juce::TextEditor::outlineColourId, juce::Colour(0x1c000000));
        midiMessagesBox.setColour(juce::TextEditor::shadowColourId, juce::Colour(0x16000000));
        addAndMakeVisible(midiMessagesBox);
    }

    ~MidiComponent() override
    {
        keyboardState.removeListener(this);
        auto activeMidiDevice = juce::MidiInput::getAvailableDevices()[midiInputList.getSelectedItemIndex()].identifier;
        deviceManager.removeMidiInputDeviceCallback(activeMidiDevice, this);
    }

    //-------------------------------------------------------------------------

    void paint(juce::Graphics& g) override
    {
        g.fillAll(juce::Colours::black);
    }

    void resized() override
    {
        auto area = getLocalBounds();

        midiInputList.setBounds(area.removeFromTop(36).reduced(10));
        keyboardComponent.setBounds(area.removeFromTop(80).reduced(10));
        midiMessagesBox.setBounds(area.reduced(10));
    }

private:
    //==============================================================================

    class IncomingMessageCallback : public juce::CallbackMessage
    {

    public:
        IncomingMessageCallback(MidiComponent* o, const juce::MidiMessage& m, const juce::String& s)
            : owner(o), message(m), source(s)
        {}

        void messageCallback() override
        {
            if (owner != nullptr)
                owner->addMessageToList(message, source);
        }

        Component::SafePointer<MidiComponent> owner;
        juce::MidiMessage message;
        juce::String source;
    };

    //-------------------------------------------------------------------------

    void addMessageToList(const juce::MidiMessage& message, const juce::String& source)
    {
        auto time = message.getTimeStamp() - startTime;

        auto hours = ((int)(time / 3600.0)) % 24;
        auto minutes = ((int)(time / 60.0)) % 60;
        auto seconds = ((int)time) % 60;
        auto millis = ((int)(time * 1000.0)) % 1000;

        auto timecode = juce::String::formatted("%02d:%02d:%02d.%03d",
            hours,
            minutes,
            seconds,
            millis);

        juce::String midiMessageString(timecode + "  -  " + message.getDescription() + " (" + source + ")");
        logMessage(midiMessageString);
    }

    void handleIncomingMidiMessage(juce::MidiInput* source, const juce::MidiMessage& message) override
    {
        const juce::ScopedValueSetter<bool> scopedInputFlag(isAddingFromMidiInput, true);
        keyboardState.processNextMidiEvent(message);
        postMessageToList(message, source->getName());
    }

    void handleNoteOn(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity) override
    {
        if (!isAddingFromMidiInput)
        {
            auto m = juce::MidiMessage::noteOn(midiChannel, midiNoteNumber, velocity);
            m.setTimeStamp(juce::Time::getMillisecondCounterHiRes() * 0.001);
            postMessageToList(m, "On-Screen Keyboard");

            if (midiNoteNumber > chord.rootLimit)
                chord.addNote(midiNoteNumber);
            else
                chord.updateRoot(midiNoteNumber);
        }
    }

    void handleNoteOff(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity) override
    {
        if (!isAddingFromMidiInput)
        {
            auto m = juce::MidiMessage::noteOff(midiChannel, midiNoteNumber);
            m.setTimeStamp(juce::Time::getMillisecondCounterHiRes() * 0.001);
            postMessageToList(m, "On-Screen Keyboard");

            if (midiNoteNumber > chord.rootLimit)
                chord.removeNote(midiNoteNumber);
            else
                chord.updateRoot(midiNoteNumber);
        }
    }

    void logMessage(const juce::String& m)
    {
        midiMessagesBox.moveCaretToEnd();
        midiMessagesBox.insertTextAtCaret(m + juce::newLine);
    }

    void postMessageToList(const juce::MidiMessage& message, const juce::String& source)
    {
        (new IncomingMessageCallback(this, message, source))->post();
    }

    /** Starts listening to a MIDI input device, enabling it if necessary. */
    void setMidiInput(int index)
    {
        auto list = juce::MidiInput::getAvailableDevices();

        deviceManager.removeMidiInputDeviceCallback(list[lastInputIndex].identifier, this);

        auto newInput = list[index];

        if (!deviceManager.isMidiInputDeviceEnabled(newInput.identifier))
            deviceManager.setMidiInputDeviceEnabled(newInput.identifier, true);

        deviceManager.addMidiInputDeviceCallback(newInput.identifier, this);
        midiInputList.setSelectedId(index + 1, juce::dontSendNotification);

        lastInputIndex = index;
    }

    //==============================================================================

    juce::AudioDeviceManager deviceManager;
    juce::ComboBox midiInputList;
    juce::Label midiInputListLabel;
    int lastInputIndex = 0;
    bool isAddingFromMidiInput = false;

    juce::MidiKeyboardState keyboardState;
    juce::MidiKeyboardComponent keyboardComponent;

    juce::TextEditor midiMessagesBox;
    double startTime;

    Chord chord = Chord();

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(MidiComponent)
};
