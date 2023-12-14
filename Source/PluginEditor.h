/*
  ==============================================================================

    PluginProcessor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  ==============================================================================
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
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    PluginEditor (AudioProcessor& audioProcessor);
    ~PluginEditor() override;

    //-- Instance Functions ---------------------------------------------------

    void resized() override;
    void changeListenerCallback(juce::ChangeBroadcaster* source) override;

private:
    //=========================================================================

    //-- Instance Variables ---------------------------------------------------

    AudioProcessor& audioProcessor;
    AudioComponent audioComponent;
    MidiProcessor midiProcessor;
    Lens lens;

};
