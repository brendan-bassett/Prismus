#include "MainComponent.h"

//==============================================================================
MainComponent::MainComponent()
{
    setSize (WIDTH_DEF, HEIGHT_DEF);
}

MainComponent::~MainComponent()
{
    // This shuts down the GL system and stops the rendering calls.
    shutdownOpenGL();
}

//==============================================================================
void MainComponent::initialise()
{
    // Initialise GL objects for rendering here.
}

void MainComponent::shutdown()
{
    // Free any GL objects created for rendering here.
}

void MainComponent::render()
{
    // This clears the context with a white background.
    juce::OpenGLHelpers::clear (juce::Colours::white);

    // Add your rendering code here...
}

//==============================================================================
void MainComponent::paint (juce::Graphics& g)
{
    g.setColour(juce::Colours::black);
    g.fillRect(0.0f, height/2, width, 2.0f);
}

void MainComponent::resized()
{
    width = getWidth();
    height = getHeight();
}
