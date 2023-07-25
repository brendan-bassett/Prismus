#include <iostream>
#include <string>

#include "Lens.h"
#include "Interval.h"


using namespace std;

// ============================================================================

Lens::Lens()
{
    setSize(width, height); 
}

// ============================================================================

void Lens::paint(juce::Graphics& g)
{
    g.fillAll(juce::Colours::white);
    g.setColour(juce::Colours::black);

    drawTonicLines(g);
    drawRootIntervals(g);

    // Left margin line
    g.fillRect(LEFT_MARGIN, 0.0f, 5.0f, height);
    g.fillRect((LEFT_MARGIN - 3.0f), 0.0f, 1.0f, height);
}

void Lens::resized()
{
    width = getWidth();
    height = getHeight();
    pxPerRelP = height / (topRelP - bottomRelP);
}

void Lens::drawRootIntervals(juce::Graphics& g)
{
    rootInterval.removeOctaves(); // Center the root interval near the prime tonic.

    // Draw all root lines above and including the prime root.
    while (rootInterval.getRelP() <= topRelP)
    {
        float rootY = relPToPx(rootInterval.getRelP());

        juce::Rectangle rootRect = juce::Rectangle(LEFT_MARGIN, rootY - 3.0f, width, 6.0f);

        juce::ColourGradient gradient = juce::ColourGradient::vertical(juce::Colours::transparentWhite,
                                                                        juce::Colours::transparentWhite, 
                                                                        rootRect);
        gradient.addColour(0.5f, juce::Colour((uint8_t)0, 165, 255, 0.6f));

        g.setGradientFill(gradient);
        g.fillRect(rootRect);

        rootInterval.translateOctaves(1);
    }

    // Again center the root interval near the prime tonic.
    rootInterval.removeOctaves();

    rootInterval.translateOctaves(-1);

    // Draw all root lines below the prime root.
    while (rootInterval.getRelP() >= bottomRelP)
    {
        float rootY = relPToPx(rootInterval.getRelP());

        juce::Rectangle rootRect = juce::Rectangle(LEFT_MARGIN, rootY - 3.0f, width, 6.0f);

        juce::ColourGradient gradient = juce::ColourGradient::vertical(juce::Colours::transparentWhite,
            juce::Colours::transparentWhite,
            rootRect);
        gradient.addColour(0.5f, juce::Colour((uint8_t)0, 165, 255, 0.6f));

        g.setGradientFill(gradient);
        g.fillRect(rootRect);

        rootInterval.translateOctaves(-1);
    }

    // Reset the gradient fill.
    g.setColour(juce::Colours::black);
}

void Lens::drawTonicLines(juce::Graphics& g)
{
    // Draw the prime tonic line in bold
    float tonicY = relPToPx(0.0f);
    g.fillRect(0.0f, tonicY, width, 3.0f);

    // Draw all tonic lines ABOVE the prime tonic.
    for (int i = 1; i <= topRelP; i++)
    {
        tonicY = relPToPx(i);

        // Draw the tonic line.
        g.fillRect(LEFT_MARGIN, tonicY, width, 1.0f);

        // Draw the indicator arrows within the left margin.
        float arrowBase = (tonicY + 6.0f);
        float arrowPtLng = (tonicY - 10.0f);
        float arrowPtSht = (tonicY - 5.0f);
            
        // Each tonic line gets a number of arrows corresponding to how many octaves it is away from the prime tonic.
        for (int j = 1; j <= abs(i); j++)
        {

            float arrowX = (LEFT_MARGIN - (j * 5.0f) - 2.0f);

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
    for (int i = -1; i >= bottomRelP; i--)
    {
        tonicY = relPToPx(i);

        // Draw the tonic line.
        g.fillRect(LEFT_MARGIN, tonicY, width, 1.0f);

        // Draw the indicator arrows within the left margin.
        float arrowBase = (tonicY - 6.0f);
        float arrowPtLng = (tonicY + 10.0f);
        float arrowPtSht = (tonicY + 5.0f);

        // Each tonic line gets a number of arrows corresponding to how many octaves it is away from the prime tonic.
        for (int j = 1; j <= abs(i); j++)
        {

            float arrowX = (LEFT_MARGIN - (j * 5.0f) - 2.0f);

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
}

float Lens::relPToPx(float relP)
{
    return (topRelP-relP) * pxPerRelP;
}
