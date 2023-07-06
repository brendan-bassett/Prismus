#include <iostream>
#include <string>

#include "Lens.h"
#include "Interval.h"


using namespace std;

// ============================================================================

Lens::Lens()
{
    setSize(width, height);

    // Test the formatting of Intervals

    DBG("Input: Interval()");
    Interval testInterval = Interval();
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(3)");
    testInterval = Interval();
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(5, 4, 2)");
    testInterval = Interval(5, 4, 2);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(5, 4, -2)");
    testInterval = Interval(5, 4, -2);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(0, 1)");
    testInterval = Interval(0, 1);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(1, 0)");
    testInterval = Interval(1, 0);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(3, 2)");
    testInterval = Interval(3, 2);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(3, 4)");
    testInterval = Interval(3, 4);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(3, 4, -2)");
    testInterval = Interval(3, 4);
    DBG("Output: " + testInterval.asString());

    DBG("Input: Interval(52, 28)");
    testInterval = Interval(52, 14);
    DBG("Output: " + testInterval.asString());
}

// ============================================================================

void Lens::paint(juce::Graphics& g)
{
    g.fillAll(juce::Colours::white);
    g.setColour(juce::Colours::black);

    drawTonicLines(g);

    // Left margin line
    g.fillRect(LEFT_MARGIN, 0.0f, 5.0f, height);
    g.fillRect((LEFT_MARGIN - 3.0f), 0.0f, 1.0f, height);
}

void Lens::resized()
{
    width = getWidth();
    height = getHeight();
}

void Lens::drawRootLines(juce::Graphics& g)
{

}

void Lens::drawTonicLines(juce::Graphics& g)
{
    // Draw the prime tonic line in bold
    float primeTonicY = (height / 2 + (octavePx * INIT_REL_P));
    g.fillRect(0.0f, primeTonicY, width, 3.0f);

    // Draw all other tonic lines and their indicator arrows.
    for (int i = -TONIC_LINES_BELOW; i <= TONIC_LINES_ABOVE; i++)
    {
        // If the tonic line is outside the drawing area, skip it.
        float tonicY = (primeTonicY - (i * octavePx));
        if (tonicY < 0 || tonicY > height)
        {
            continue;
        }

        // Draw the tonic line.
        g.fillRect(LEFT_MARGIN, tonicY, width, 1.0f);

        // Draw the indicator arrows within the left margin.

        // Flip the arrows for upper vs lower tonic lines.
        float arrowBase = 0.0f;
        float arrowPtLng = 0.0f;
        float arrowPtSht = 0.0f;
        if (i > 0)
        {
            arrowBase = (tonicY + 6.0f);
            arrowPtLng = (tonicY - 10.0f);
            arrowPtSht = (tonicY - 5.0f);
        }
        else
        {
            arrowBase = (tonicY - 6.0f);
            arrowPtLng = (tonicY + 10.0f);
            arrowPtSht = (tonicY + 5.0f);
        }

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