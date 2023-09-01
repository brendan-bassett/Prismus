/*
  ==============================================================================

    This file contains the basic framework code for a JUCE plugin editor.

  ==============================================================================
*/

#pragma once

#include "PluginEditor.h"

//==============================================================================
PrismusAudioProcessorEditor::PrismusAudioProcessorEditor (PrismusAudioProcessor& p)
    : AudioProcessorEditor (&p), audioProcessor (p)
{
    setSize(480, 800);
    addAndMakeVisible(lens);
    addAndMakeVisible(audioComponent);
}

PrismusAudioProcessorEditor::~PrismusAudioProcessorEditor()
{
}

//==============================================================================
void PrismusAudioProcessorEditor::paint (juce::Graphics& g)
{
    // (Our component is opaque, so we must completely fill the background with a solid colour)
    g.fillAll (getLookAndFeel().findColour (juce::ResizableWindow::backgroundColourId));

    g.setColour (juce::Colours::white);
    g.setFont (15.0f);
    g.drawFittedText ("Hello World!", getLocalBounds(), juce::Justification::centred, 1);
}

void PrismusAudioProcessorEditor::resized()
{
    // This is generally where you'll want to lay out the positions of any
    // subcomponents in your editor..
}
