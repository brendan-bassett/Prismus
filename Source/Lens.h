/*
  ==============================================================================

    Lens.h
    Created: 1 Sep 2023 2:59:01pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>
#include "Interval.h"

/// @brief Displays chords in relative intonation.
class Lens : public juce::Component
{

public:
    //==================================================================================================================

    Lens(Chord* chord);

    //------------------------------------------------------------------------------------------------------------------

    /// @copydoc Component::paint()
    void paint(juce::Graphics& g) override;

    /// @copydoc Component::paint()
    void resized() override;

    Chord* chord;

private:
    //==================================================================================================================

    void drawChords(juce::Graphics& g);

    void drawRootIntervals(juce::Graphics& g);

    void drawTonicLines(juce::Graphics& g);

    float relPToPx(float relP);

    //------------------------------------------------------------------------------------------------------------------

    const float LEFT_MARGIN = 30.0f;

    Interval rootInterval = Interval(3, 2);

    //------------------------------------------------------------------------------------------------------------------

    float topRelP = 5.0f;
    float bottomRelP = -2.2f;
    float pxPerRelP = 0.0f;

    std::forward_list<Interval> chordList = std::forward_list<Interval>();

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Lens)

};
