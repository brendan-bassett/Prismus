/*
  =================================================================================================================

    PluginProcessor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  =================================================================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "PluginProcessor.h"
#include "Lens.h"
#include "AudioComponent.h"
#include "MidiProcessor.h"

//==============================================================================
/**
*/
class PrismusAudioProcessorEditor  : public juce::AudioProcessorEditor
{
public:
    PrismusAudioProcessorEditor (PrismusAudioProcessor&);
    ~PrismusAudioProcessorEditor() override;

    //==============================================================================
    void resized() override;

private:
    PrismusAudioProcessor& audioProcessor;
    Lens lens;
    AudioComponent audioComponent;
    MidiComponent midiProcessor;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (PrismusAudioProcessorEditor)
};
