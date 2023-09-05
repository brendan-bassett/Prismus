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

void PrismusAudioProcessorEditor::resized()
{
    lens.setBounds(0, 0, getWidth() - 240, getHeight());
    audioComponent.setBounds(240, 0, getWidth() - 240, getHeight());
}
