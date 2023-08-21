/*
  ==============================================================================

    MainComponent.h
    Created: 12 Aug 2023 1:18:42pm
    Author:  bbass

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "Lens.h"
#include "AudioComponent.h"

//==============================================================================
/*
*/
class MainComponent  : public juce::Component
{
public:
    MainComponent()
    {
        setSize(480, 800);
        addAndMakeVisible(lens);
        addAndMakeVisible(audioComponent);
    }

    ~MainComponent() override
    {
    }

    void paint (juce::Graphics& g) override
    {
    }

    void resized() override
    {
        lens.setBounds(0, 0, getWidth() - 240, getHeight());
        audioComponent.setBounds(240, 0, getWidth() - 240, getHeight());
    }

private:
    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(MainComponent)

    Lens lens;
    AudioComponent audioComponent;

};
