/*
  =================================================================================================================

    PluginProcessor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  =================================================================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "AudioProcessor.h"
#include "Lens.h"
#include "AudioComponent.h"
#include "MidiProcessor.h"

class PluginEditor  : public juce::AudioProcessorEditor,
                      public juce::ChangeListener
{

public:
    //==============================================================================

    PluginEditor (AudioProcessor&);
    ~PluginEditor() override;

    void resized() override;

    void changeListenerCallback(juce::ChangeBroadcaster* source) override;

private:
    //==============================================================================

    AudioProcessor& audioProcessor;
    AudioComponent audioComponent;
    MidiProcessor midiProcessor;
    Lens lens;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (PluginEditor)
};
