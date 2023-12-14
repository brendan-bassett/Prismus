/*
  ==============================================================================

    The MAIN WINDOW for editing the plugin functionality.

  ==============================================================================
*/

#pragma once

#include "PluginEditor.h"
#include "Chord.h"

//==============================================================================
PluginEditor::PluginEditor (AudioProcessor& p) : AudioProcessorEditor (&p),
                                                    audioProcessor (p), 
                                                    midiProcessor(p),
                                                    lens(&midiProcessor.chord)
{
    setSize(720, 800);
    addAndMakeVisible(audioComponent);
    addAndMakeVisible(midiProcessor);
    addAndMakeVisible(lens);
}

PluginEditor::~PluginEditor()
{
}

void PluginEditor::changeListenerCallback(juce::ChangeBroadcaster* source)
{
    if (typeid(source) == typeid(MidiProcessor))
        &midiProcessor.chord;
}

void PluginEditor::resized()
{
    auto area = getLocalBounds();
    auto rightArea = area.removeFromRight(480);

    lens.setBounds(area);
    audioComponent.setBounds(rightArea.removeFromTop(160));
    midiProcessor.setBounds(rightArea);
}
