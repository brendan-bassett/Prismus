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

#include "AudioProcessor.h"
#include "Chord.h"
#include "PluginEditor.h"

//==============================================================================

class MidiProcessor : public juce::Component,
                      public juce::ChangeBroadcaster,
                      private juce::MidiInputCallback,
                      private juce::MidiKeyboardStateListener
{

public:
    //==============================================================================

    MidiProcessor(AudioProcessor& audioProcessor, PluginEditor& pluginEditor);

    ~MidiProcessor() override;

    //-------------------------------------------------------------------------

    void paint(juce::Graphics& g) override;

    void resized() override;

    Chord& getActiveChord();

private:
    //==============================================================================

    class IncomingMessageCallback : public juce::CallbackMessage
    {

    public:
        IncomingMessageCallback(MidiProcessor* o, const juce::MidiMessage& m, const juce::String& s)
            : owner(o), message(m), source(s)
        {}

        void messageCallback() override
        {
            if (owner != nullptr)
                owner->addMessageToList(message, source);
        }

        Component::SafePointer<MidiProcessor> owner;
        juce::MidiMessage message;
        juce::String source;
    };

    //-------------------------------------------------------------------------

    void addMessageToList(const juce::MidiMessage& message, const juce::String& source);

    void handleIncomingMidiMessage(juce::MidiInput* source, const juce::MidiMessage& message) override;

    void handleNoteOn(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity) override;

    void handleNoteOff(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity) override;

    void logMessage(const juce::String& m);

    void postMessageToList(const juce::MidiMessage& message, const juce::String& source);

    void setMidiInput(int index);

    //-------------------------------------------------------------------------

    AudioProcessor& audioProcessor;

    juce::AudioDeviceManager deviceManager;
    juce::ComboBox midiInputList;
    juce::Label midiInputListLabel;
    int lastInputIndex = 0;
    bool isAddingFromMidiInput = false;

    juce::MidiKeyboardState keyboardState;
    juce::MidiKeyboardComponent keyboardComponent;

    juce::TextEditor midiMessagesBox;
    double startTime;

    Chord activeChord;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(MidiProcessor)
};
