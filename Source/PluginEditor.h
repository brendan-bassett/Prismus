/*
  ==============================================================================

    This file contains the basic framework code for a JUCE plugin editor.

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "PluginProcessor.h"
#include "Lens.h"
#include "AudioComponent.h"

//==============================================================================
/**
*/
class PrismusAudioProcessorEditor  : public juce::AudioProcessorEditor
{
public:
    PrismusAudioProcessorEditor (PrismusAudioProcessor&);
    ~PrismusAudioProcessorEditor() override;

    //==============================================================================
    void paint (juce::Graphics&) override;
    void resized() override;

private:
    PrismusAudioProcessor& audioProcessor;
    Lens lens;
    AudioComponent audioComponent;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (PrismusAudioProcessorEditor)
};
