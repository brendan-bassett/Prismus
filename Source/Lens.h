#pragma once

#include <JuceHeader.h>

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

    //-----------------------------------------------------------------------------------------------------------------------

    const int TONIC_LINES_ABOVE = 5;
    const int TONIC_LINES_BELOW = 3;

    const float LEFT_MARGIN = 30.0f;    // ...in Pixels
    const float INIT_REL_P = 1.0f;      // ...in Octaves

    //-----------------------------------------------------------------------------------------------------------------------

    float height = 800.0f;
    float width = 240.0f;
    float octavePx = 120.0f;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Lens)

};
