#include "Score.h"
#include "TonicButton.h"

Score::Score()
{
    setSize (width, height);

    TonicButton tonicButton = TonicButton(1);
}

//==============================================================================
void Score::paint (juce::Graphics& g)
{
    g.fillAll(juce::Colours::white);
    g.setColour(juce::Colours::black);
    g.fillRect(0.0f, height/2, width, 2.0f);
}

void Score::resized()
{
    width = getWidth();
    height = getHeight();
}
