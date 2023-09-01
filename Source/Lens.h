/*
  ==============================================================================

    Lens.h
    Created: 1 Sep 2023 2:59:01pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>
#include <forward_list>

#include "Interval.h"

/// @brief Displays chords in relative intonation.
class Lens : public juce::Component
{

public:
    //==================================================================================================================

    Lens();

    //------------------------------------------------------------------------------------------------------------------

    /// @copydoc Component::paint()
    void paint(juce::Graphics& g) override;

    /// @copydoc Component::paint()
    void resized() override;

private:
    //==================================================================================================================

    /// @brief Draw all notes in the current chord structure.
    /// @param g The juce Graphics object used for rendering.
    void drawChords(juce::Graphics& g);

    /// @brief Draw the lines for the root of the current chord structure.
    /// @param g The juce Graphics object used for rendering.
    void drawRootIntervals(juce::Graphics& g);

    /// @brief Draw the tonic lines and their corresponding arrows.
    /// @param g The juce Graphics object used for rendering.
    void drawTonicLines(juce::Graphics& g);

    /// @brief Converts vertical location from relative pitch space to pixel space.
    /// @param relP The relative pitch location.
    /// @return The location in pixels on the Y-axis of the component.
    float relPToPx(float relP);

    //------------------------------------------------------------------------------------------------------------------

    /// @brief The width in pixels of the leftmost margin where arrows for tonic lines are drawn.
    const float LEFT_MARGIN = 30.0f;

    /// @brief The interval for the root of the current chord structure.
    Interval rootInterval = Interval(3, 2);

    //------------------------------------------------------------------------------------------------------------------

    /// @brief The top of the component in relative pitch space.
    float topRelP = 5.0f;
    /// @brief The bottom of the component in relative pitch space.
    float bottomRelP = -2.2f;
    /// @brief The number of pixels corresponding to a relative pitch distance of 1.
    float pxPerRelP = 0.0f;

    std::forward_list<Interval> chordList = std::forward_list<Interval>();

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(Lens)

};
