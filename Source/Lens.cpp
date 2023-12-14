/*
  ==============================================================================

    Lens.h
    Created: 1 Sep 2023 2:59:01pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "Chord.h"
#include "Lens.h"

// PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

Lens::Lens(Chord& chord): chordPtr(chord)
{}

//-- Instance Functions -------------------------------------------------------

void Lens::paint(juce::Graphics& g)
{
    g.fillAll(juce::Colours::white);
    g.setColour(juce::Colours::black);

    drawTonicLines(g);
    drawRootIntervals(g);

    // Left margin line
    g.fillRect(LEFT_MARGIN, 0.f, 5.f, (float)getHeight());
    g.fillRect((LEFT_MARGIN - 3.f), 0.f, 1.f, (float)getHeight());

    drawChords(g);
}

void Lens::resized()
{
    pxPerRelP = (float)getHeight() / (topRelP - bottomRelP);
}

// PRIVATE
//=============================================================================

void Lens::drawChords(juce::Graphics& g)
{
    std::forward_list<float> notesRelP { chordPtr->getNotesRelP() };

    for (auto nrp{ notesRelP.begin() }; nrp != notesRelP.end(); ++nrp)
    {
        if (*nrp > topRelP || *nrp < bottomRelP)
        {
            continue;
        }

        float noteY{ relPToPx(*nrp) };

        juce::Rectangle noteRect{ juce::Rectangle(LEFT_MARGIN + 50.f,
            noteY - 4.f,
            getWidth() - LEFT_MARGIN - (50.f * 2),
            8.f) };

        g.drawRect(noteRect, 1.0f);

        Interval harmonic{ Interval(2, 1) };
        float harmonicY{ relPToPx(*nrp + harmonic.getRelP()) };
        float harmonicInsetX{ 0.f };

        for (int h{ 2 }; harmonicY > 0; h++)
        {
            harmonicInsetX = harmonicInsetX + (18.0f / h);
            g.drawLine(noteRect.getX() + harmonicInsetX,
                harmonicY,
                noteRect.getRight() - harmonicInsetX,
                harmonicY, 2.0f);

            harmonic = Interval(h + 1, 1);
            harmonicY = relPToPx(*nrp + harmonic.getRelP());

        }

    }
}

void Lens::drawRootIntervals(juce::Graphics& g)
{
    // Center the root interval near the prime tonic.
    rootInterval = rootInterval.removeOctaves();

    g.setFont(20.0f); // 20pf font

    // Draw all root lines above and including the prime root.
    while (rootInterval.getRelP() <= topRelP)
    {
        float rootY{ relPToPx(rootInterval.getRelP()) };

        juce::Rectangle rootRect{ juce::Rectangle(LEFT_MARGIN, rootY - 3.f, (float)getWidth(), 6.f) };

        juce::ColourGradient gradient{ juce::ColourGradient::vertical(juce::Colours::transparentWhite,
            juce::Colours::transparentWhite,
            rootRect) };
        gradient.addColour(0.5f, juce::Colour((uint8_t)0, 165, 255, 0.6f));

        g.setGradientFill(gradient);
        g.fillRect(rootRect);

        std::string shorthand{ rootInterval.asShorthand() };

        rootInterval = rootInterval.translateOctaves(1);
    }

    // Draw all root lines below the prime root.
    rootInterval = rootInterval.removeOctaves();
    rootInterval = rootInterval.translateOctaves(-1);

    while (rootInterval.getRelP() >= bottomRelP)
    {
        float rootY{ relPToPx(rootInterval.getRelP()) };

        juce::Rectangle rootRect{ juce::Rectangle(LEFT_MARGIN, rootY - 3.f, (float)getWidth(), 6.f) };

        juce::ColourGradient gradient{ juce::ColourGradient::vertical(juce::Colours::transparentWhite,
            juce::Colours::transparentWhite,
            rootRect) };
        gradient.addColour(0.5f, juce::Colour((uint8_t)0, 165, 255, 0.6f));

        g.setGradientFill(gradient);
        g.fillRect(rootRect);

        rootInterval = rootInterval.translateOctaves(-1);
    }

    // Reset the gradient fill.
    g.setColour(juce::Colours::black);

    // Center the root interval near the prime tonic.
    rootInterval = rootInterval.removeOctaves();
}

void Lens::drawTonicLines(juce::Graphics& g)
{
    // Draw the prime tonic line in bold
    float tonicY{ relPToPx(0.0f) };
    g.fillRect(0.f, tonicY, (float)getWidth(), 3.f);

    // Draw all tonic lines ABOVE the prime tonic.
    for (int i{ 1 }; i <= topRelP; i++)
    {
        tonicY = relPToPx(i);

        // Draw the tonic line.
        g.fillRect(LEFT_MARGIN, tonicY, (float)getWidth(), 1.f);

        // Draw the indicator arrows within the left margin.
        float arrowBase{ tonicY + 6.0f };
        float arrowPtLng{ tonicY - 10.0f };
        float arrowPtSht{ tonicY - 5.0f };

        // Each tonic line gets a number of arrows corresponding to how many octaves it is away from the prime tonic.
        for (int j{ 1 }; j <= abs(i); j++)
        {

            float arrowX{ LEFT_MARGIN - (j * 5.f) - 2.f };

            // Alternate the lenths of the arrows for greater readability.
            if ((j % 2) == 0)
            {
                juce::Line arrwLine = juce::Line<float>(arrowX, arrowBase, arrowX, arrowPtLng);
                g.drawArrow(arrwLine, 1.0f, 5.0f, 5.0f);
            }
            else
            {
                juce::Line arrwLine = juce::Line<float>(arrowX, arrowBase, arrowX, arrowPtSht);
                g.drawArrow(arrwLine, 1.0f, 5.0f, 5.0f);
            }
        }
    }

    // Draw all tonic lines BELOW the prime tonic.
    for (int i{ -1 }; i >= bottomRelP; i--)
    {
        tonicY = relPToPx(i);

        // Draw the tonic line.
        g.fillRect(LEFT_MARGIN, tonicY, (float)getWidth(), 1.0f);

        // Draw the indicator arrows within the left margin.
        float arrowBase{ tonicY - 6.0f };
        float arrowPtLng{ tonicY + 10.0f };
        float arrowPtSht{ tonicY + 5.0f };

        // Each tonic line gets a number of arrows corresponding to how many octaves it is away from the prime tonic.
        for (int j{ 1 }; j <= abs(i); j++)
        {

            float arrowX{ LEFT_MARGIN - (j * 5.0f) - 2.0f };

            // Alternate the lenths of the arrows for greater readability.
            if ((j % 2) == 0)
            {
                juce::Line arrwLine { juce::Line<float>(arrowX, arrowBase, arrowX, arrowPtLng) };
                g.drawArrow(arrwLine, 1.0f, 5.0f, 5.0f);
            }
            else
            {
                juce::Line arrwLine { juce::Line<float>(arrowX, arrowBase, arrowX, arrowPtSht) };
                g.drawArrow(arrwLine, 1.0f, 5.0f, 5.0f);
            }
        }
    }
}

float Lens::relPToPx(float relP)
{
    return (topRelP - relP) * pxPerRelP;
}

// NON-MEMBER
//=============================================================================

const static float LEFT_MARGIN{ 30.0f };
