/*
  ==============================================================================

    PluginEditor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "PluginEditor.h"
#include "Chord.h"

// PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

PluginEditor::PluginEditor (AudioProcessor& p) : AudioProcessorEditor (&p),
                                                    audioProcessor (p), 
                                                    midiProcessor(p, this),
                                                    lens(midiProcessor.getActiveChord())
{
    setSize(720, 800);
    addAndMakeVisible(audioComponent);
    addAndMakeVisible(midiProcessor);
    addAndMakeVisible(lens);
}

PluginEditor::~PluginEditor()
{
}

//-- Instance Functions -------------------------------------------------------

void PluginEditor::changeListenerCallback(juce::ChangeBroadcaster* source)
{
    if (typeid(source) == typeid(MidiProcessor))
        lens.repaint();
}

void PluginEditor::resized()
{
    auto area = getLocalBounds();
    auto rightArea = area.removeFromRight(480);

    lens.setBounds(area);
    audioComponent.setBounds(rightArea.removeFromTop(160));
    midiProcessor.setBounds(rightArea);
}
