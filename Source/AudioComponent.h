/*
  ==============================================================================

    AudioComponent.h
    Created: 12 Aug 2023 12:49:02pm
    Author:  bbass

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

//==============================================================================
/*
*/
class AudioComponent  : public juce::AudioAppComponent
{
public:
    AudioComponent()
    {
        levelSlider.setRange(0.0, 0.05);
        levelSlider.setTextBoxStyle(juce::Slider::TextBoxRight, false, 40, 20);
        levelLabel.setText("Noise Level", juce::dontSendNotification);

        addAndMakeVisible(levelSlider);
        addAndMakeVisible(levelLabel);

        setAudioChannels(2, 2);
    }

    ~AudioComponent() override
    {
        shutdownAudio();
    }

    void prepareToPlay(int, double) override {}

    //! [getNextAudioBlock]
    void getNextAudioBlock(const juce::AudioSourceChannelInfo& bufferToFill) override
    {
        auto* device = deviceManager.getCurrentAudioDevice();
        auto activeInputChannels = device->getActiveInputChannels();
        auto activeOutputChannels = device->getActiveOutputChannels();
        //! [getNextAudioBlock]

        //! [getNextAudioBlock 2]
        auto maxInputChannels = activeInputChannels.getHighestBit() + 1;
        auto maxOutputChannels = activeOutputChannels.getHighestBit() + 1;
        //! [getNextAudioBlock 2]

        //! [getNextAudioBlock 3]
        auto level = (float)levelSlider.getValue();

        for (auto channel = 0; channel < maxOutputChannels; ++channel)
        {
            if ((!activeOutputChannels[channel]) || maxInputChannels == 0)
            {
                bufferToFill.buffer->clear(channel, bufferToFill.startSample, bufferToFill.numSamples);
            }
            //! [getNextAudioBlock 3]

            //! [getNextAudioBlock 4]
            else
            {
                auto actualInputChannel = channel % maxInputChannels; // [1]

                if (!activeInputChannels[channel]) // [2]
                {
                    bufferToFill.buffer->clear(channel, bufferToFill.startSample, bufferToFill.numSamples);
                }
                else // [3]
                {
                    auto* inBuffer = bufferToFill.buffer->getReadPointer(actualInputChannel,
                        bufferToFill.startSample);
                    auto* outBuffer = bufferToFill.buffer->getWritePointer(channel, bufferToFill.startSample);

                    for (auto sample = 0; sample < bufferToFill.numSamples; ++sample)
                    {
                        auto noise = (random.nextFloat() * 2.0f) - 1.0f;
                        outBuffer[sample] = inBuffer[sample] + (inBuffer[sample] * noise * level);
                    }
                }
            }
        }
    }
    //! [getNextAudioBlock 4]

    void releaseResources() override {}

    void resized() override
    {
        levelLabel.setBounds(0, 0, 70, 20);
        levelSlider.setBounds(80, 0, getWidth() - 90, 20);
    }

private:
    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (AudioComponent)

    juce::Random random;
    juce::Slider levelSlider;
    juce::Label levelLabel;
};
