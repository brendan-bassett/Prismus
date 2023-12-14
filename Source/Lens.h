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


class Lens : public juce::Component
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    Lens(Chord& chord);

    //-- Instance Functions ---------------------------------------------------

    void paint(juce::Graphics& g) override;
    void resized() override;

private:
    //=========================================================================

    //-- Instance Functions ---------------------------------------------------

    void drawChords(juce::Graphics& g);
    void drawRootIntervals(juce::Graphics& g);
    void drawTonicLines(juce::Graphics& g);
    float relPToPx(float relP);

    //-- Instance Variables ---------------------------------------------------

    Chord* chordPtr = nullptr;

    Interval rootInterval = Interval(3, 2);

    float topRelP = 5.0f;
    float bottomRelP = -2.2f;
    float pxPerRelP = 0.0f;

    //-------------------------------------------------------------------------

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Lens)

};
