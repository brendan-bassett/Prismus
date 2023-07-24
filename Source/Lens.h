#pragma once

#include <JuceHeader.h>

#include "Interval.h"

/// @brief Displays chords in relative intonation.

class Lens  : public juce::Component
{

public:
    //=======================================================================================================================
    
    Lens();

    //-----------------------------------------------------------------------------------------------------------------------

    void paint (juce::Graphics& g) override;
    void resized() override;

private:
    //=======================================================================================================================

    void drawRootLines(juce::Graphics& g);
    void drawTonicLines(juce::Graphics& g);

    float relPToPx(float relP);

    //-----------------------------------------------------------------------------------------------------------------------

    const float LEFT_MARGIN = 30.0f;    // ...in Pixels

    Interval primeRootInterval = Interval();

    //-----------------------------------------------------------------------------------------------------------------------

    float height = 800.0f;
    float width = 240.0f;

    float topRelP = 5.0f;
    float bottomRelP = -2.2f;
    float pxPerRelP = 0.0f;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Lens)

};
