/*
  ==============================================================================

    AudioProcessor.h
    Created: 16 Aug 2023 10:00:12pm
    Author:  bbass

  ==============================================================================
*/

#pragma once

#include <iostream>
#include <JuceHeader.h>
//#include <src/common/RingBuffer.h>
//#include "rubberband/RubberBandStretcher.h"

//using RubberBand::RubberBandStretcher;
//using RubberBand::RingBuffer;

//==============================================================================
/* CLASS: AudioComponent
*/
class AudioPluginAudioProcessor : public juce::AudioProcessor
{

public:

    //==============================================================================
    void prepareToPlay(double sampleRate, int samplesPerBlock) override
    {
        /*
        // "Dynamic memory": Memory that is allocated while the app is running.
        rubberband = std::make_unique<RubberBandStretcher>(sampleRate,
            getTotalNumOutputChannels(),
            RubberBandStretcher::PresetOption::DefaultOptions,
            0.5,
            0.5);

        rubberband->reset();
        */
    }

    void processBlock(juce::AudioBuffer<float>& buffer, juce::MidiBuffer& midiBuffer) override
    {
        /*
        * FROM "Livestream - Implementing a TimeStretching Library (RubberBand)"  by The Audio Programmer 
        * https://www.youtube.com/watch?v=XhmM8HZj7aU
        * 
        int totalNumInputChannels = getTotalNumInputChannels();
        int totalNumOutputChannels = getTotalNumInputChannels();

        for (int i = totalNumInputChannels; i < totalNumOutputChannels; ++i)
            buffer.clear(i, 0, buffer.getNumSamples());

        if (params.getParameterAsValue("Play") == true)
        {
            if (audioFileSource != nullptr)
                transportSource.start();

        }
        else
        {
            transportSource.stop();
            transportSource.setPosition(0.0);
        }

        auto readPointers = buffer.getArrayOfReadPointers();
        auto writePointers = buffer.getArrayOfWritePointers();

        rubberband->process(readPointers, buffer.getNumSamples(), false);

        auto samplesAvailable = rubberband->available();

        if ()
            auto rubberbandOutput = rubberband->retrieve(writePointers, buffer.getNumSamples());

        transportSource.getNextAudioBlock(AudioSourceChannelInfo(buffer));
        */
    }

    using AudioProcessor::processBlock;

private:

    //std::unique_ptr< RubberBandStretcher> rubberband;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(AudioPluginAudioProcessor)
};
