/*
  ==============================================================================

    MidiProcessor.cpp
    Created: 13 Dec 2023 6:48:38pm
    Author:  bbass

  ==============================================================================
*/

#include "MidiProcessor.h"

MidiProcessor::MidiProcessor(AudioProcessor& audioProcessor, PluginEditor& pluginEditor)
    : audioProcessor(audioProcessor),
    keyboardComponent(keyboardState, juce::MidiKeyboardComponent::horizontalKeyboard),
    startTime(juce::Time::getMillisecondCounterHiRes() * 0.001)
{
    addChangeListener(&audioProcessor);
    addChangeListener(&audioProcessor);

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

MidiProcessor::~MidiProcessor()
{
    keyboardState.removeListener(this);
    auto activeMidiDevice = juce::MidiInput::getAvailableDevices()[midiInputList.getSelectedItemIndex()].identifier;
    deviceManager.removeMidiInputDeviceCallback(activeMidiDevice, this);
}

//-------------------------------------------------------------------------

void MidiProcessor::paint(juce::Graphics& g)
{
    g.fillAll(juce::Colours::black);
}

void MidiProcessor::resized()
{
    auto area = getLocalBounds();

    midiInputList.setBounds(area.removeFromTop(36).reduced(10));
    keyboardComponent.setBounds(area.removeFromTop(80).reduced(10));
    midiMessagesBox.setBounds(area.reduced(10));
}

Chord& MidiProcessor::getActiveChord()
{
    return activeChord;
}

//-------------------------------------------------------------------------

void MidiProcessor::addMessageToList(const juce::MidiMessage& message, const juce::String& source)
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

void MidiProcessor::handleIncomingMidiMessage(juce::MidiInput* source, const juce::MidiMessage& message)
{
    const juce::ScopedValueSetter<bool> scopedInputFlag(isAddingFromMidiInput, true);
    keyboardState.processNextMidiEvent(message);
    postMessageToList(message, source->getName());
}

void MidiProcessor::handleNoteOn(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity)
{
    if (!isAddingFromMidiInput)
    {
        auto m = juce::MidiMessage::noteOn(midiChannel, midiNoteNumber, velocity);
        m.setTimeStamp(juce::Time::getMillisecondCounterHiRes() * 0.001);
        postMessageToList(m, "On-Screen Keyboard");

        if (midiNoteNumber > activeChord.rootLimit)
            activeChord.addNote(midiNoteNumber);
        else
            activeChord.updateRoot(midiNoteNumber);

        sendChangeMessage();
    }
}

void MidiProcessor::handleNoteOff(juce::MidiKeyboardState*, int midiChannel, int midiNoteNumber, float velocity)
{
    if (!isAddingFromMidiInput)
    {
        auto m = juce::MidiMessage::noteOff(midiChannel, midiNoteNumber);
        m.setTimeStamp(juce::Time::getMillisecondCounterHiRes() * 0.001);
        postMessageToList(m, "On-Screen Keyboard");

        if (midiNoteNumber > activeChord.rootLimit)
            activeChord.removeNote(midiNoteNumber);
        else
            activeChord.updateRoot(midiNoteNumber);

        sendChangeMessage();
    }
}

void MidiProcessor::logMessage(const juce::String& m)
{
    midiMessagesBox.moveCaretToEnd();
    midiMessagesBox.insertTextAtCaret(m + juce::newLine);
}

void MidiProcessor::postMessageToList(const juce::MidiMessage& message, const juce::String& source)
{
    (new IncomingMessageCallback(this, message, source))->post();
}

/** Starts listening to a MIDI input device, enabling it if necessary. */
void MidiProcessor::setMidiInput(int index)
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
