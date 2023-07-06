#pragma once

#include <JuceHeader.h>

//==============================================================================
/*
    This component lives inside our window, and this is where you should put all
    your controls and content.
*/

class Score  : public juce::Component
{

public:
    Score();

    //==============================================================================
    void paint (juce::Graphics& g) override;
    void resized() override;

private:
    //==============================================================================

    const int   SCORE_START_MARGIN = 60;    // ...in Pixels
    const float INIT_REL_P = -0.5f; // ...in Octaves

    float height = 1080.0f;
    float width = 720.0f;


    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Score)

    /// @brief The horizontal alignment of an object drawn on the score.
    enum HAlign
    {
    left,
    center,
    right
    };

    /// @brief The vertical alignment of an object drawn on the score.
    enum VAlign
    {
    bottom,
    center,
    top
    };

};
