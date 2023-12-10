/*
  ==============================================================================

    The MAIN WINDOW for editing the plugin functionality.

  ==============================================================================
*/

#pragma once

#include "PluginEditor.h"

//==============================================================================
PrismusAudioProcessorEditor::PrismusAudioProcessorEditor (PrismusAudioProcessor& p)
    : AudioProcessorEditor (&p), audioProcessor (p)
{
    //setTopLeftPosition(200, 200);
    setSize(720, 800);
    addAndMakeVisible(lens);
    addAndMakeVisible(audioComponent);
    addAndMakeVisible(midiProcessor);
}

PrismusAudioProcessorEditor::~PrismusAudioProcessorEditor()
{
}

void PrismusAudioProcessorEditor::resized()
{
    auto area = getLocalBounds();
    auto rightArea = area.removeFromRight(480);

    lens.setBounds(area);
    audioComponent.setBounds(rightArea.removeFromTop(160));
    midiProcessor.setBounds(rightArea);
}
