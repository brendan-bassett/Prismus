/*
  =================================================================================================================

    PluginProcessor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  =================================================================================================================
*/

#pragma once

#include <iostream>

#include "PluginProcessor.h"
#include "PluginEditor.h"

#include <src/common/RingBuffer.h>
#include "rubberband/RubberBandStretcher.h"

using RubberBand::RubberBandStretcher;
using RubberBand::RingBuffer;

//==============================================================================
PrismusAudioProcessor::PrismusAudioProcessor()
#ifndef JucePlugin_PreferredChannelConfigurations
     : AudioProcessor (BusesProperties()
                     #if ! JucePlugin_IsMidiEffect
                      #if ! JucePlugin_IsSynth
                       .withInput  ("Input",  juce::AudioChannelSet::stereo(), true)
                      #endif
                       .withOutput ("Output", juce::AudioChannelSet::stereo(), true)
                     #endif
                       )
#endif
{
}

PrismusAudioProcessor::~PrismusAudioProcessor()
{
}

//==============================================================================
const juce::String PrismusAudioProcessor::getName() const
{
    return JucePlugin_Name;
}

bool PrismusAudioProcessor::acceptsMidi() const
{
   #if JucePlugin_WantsMidiInput
    return true;
   #else
    return false;
   #endif
}

bool PrismusAudioProcessor::producesMidi() const
{
   #if JucePlugin_ProducesMidiOutput
    return true;
   #else
    return false;
   #endif
}

bool PrismusAudioProcessor::isMidiEffect() const
{
   #if JucePlugin_IsMidiEffect
    return true;
   #else
    return false;
   #endif
}

double PrismusAudioProcessor::getTailLengthSeconds() const
{
    return 0.0;
}

int PrismusAudioProcessor::getNumPrograms()
{
    return 1;   // NB: some hosts don't cope very well if you tell them there are 0 programs,
                // so this should be at least 1, even if you're not really implementing programs.
}

int PrismusAudioProcessor::getCurrentProgram()
{
    return 0;
}

void PrismusAudioProcessor::setCurrentProgram (int index)
{
}

const juce::String PrismusAudioProcessor::getProgramName (int index)
{
    return {};
}

void PrismusAudioProcessor::changeProgramName (int index, const juce::String& newName)
{
}

//==============================================================================
void PrismusAudioProcessor::prepareToPlay (double sampleRate, int samplesPerBlock)
{

    DBG("TOTAL NUMBER INPUT CHANNELS: " << getTotalNumInputChannels());
    DBG("TOTAL NUMBER OUTPUT CHANNELS: " << getTotalNumOutputChannels());

    rbBuffer1.setSize(getTotalNumInputChannels(), samplesPerBlock);
    rbBuffer2.setSize(getTotalNumInputChannels(), samplesPerBlock);
    rbBuffer3.setSize(getTotalNumInputChannels(), samplesPerBlock);

    // "Dynamic memory": Memory that is allocated while the app is running.
    rubberband1 = std::make_unique<RubberBandStretcher>(sampleRate,
        getTotalNumOutputChannels(),
        RubberBandStretcher::PresetOption::DefaultOptions | RubberBandStretcher::Option::OptionProcessRealTime,
        1.0,
        1.25);

    // "Dynamic memory": Memory that is allocated while the app is running.
    rubberband2 = std::make_unique<RubberBandStretcher>(sampleRate,
        getTotalNumOutputChannels(),
        RubberBandStretcher::PresetOption::DefaultOptions | RubberBandStretcher::Option::OptionProcessRealTime,
        1.0,
        1.5);

    // "Dynamic memory": Memory that is allocated while the app is running.
    rubberband3 = std::make_unique<RubberBandStretcher>(sampleRate,
        getTotalNumOutputChannels(),
        RubberBandStretcher::PresetOption::DefaultOptions | RubberBandStretcher::Option::OptionProcessRealTime,
        1.0,
        1.75);

    rubberband1->reset();
    rubberband2->reset();
    rubberband3->reset();
}

void PrismusAudioProcessor::releaseResources()
{
    // When playback stops, you can use this as an opportunity to free up any
    // spare memory, etc.
}

#ifndef JucePlugin_PreferredChannelConfigurations
bool PrismusAudioProcessor::isBusesLayoutSupported (const BusesLayout& layouts) const
{
  #if JucePlugin_IsMidiEffect
    juce::ignoreUnused (layouts);
    return true;
  #else
    // This is the place where you check if the layout is supported.
    // In this template code we only support mono or stereo.
    // Some plugin hosts, such as certain GarageBand versions, will only
    // load plugins that support stereo bus layouts.
    if (layouts.getMainOutputChannelSet() != juce::AudioChannelSet::mono()
     && layouts.getMainOutputChannelSet() != juce::AudioChannelSet::stereo())
        return false;

    // This checks if the input layout matches the output layout
   #if ! JucePlugin_IsSynth
    if (layouts.getMainOutputChannelSet() != layouts.getMainInputChannelSet())
        return false;
   #endif

    return true;
  #endif
}
#endif

void PrismusAudioProcessor::processBlock (juce::AudioBuffer<float>& ioBuffer, juce::MidiBuffer& midiMessages)
{
    /*
    * FROM "Livestream - Implementing a TimeStretching Library (RubberBand)"  by The Audio Programmer
    * https://www.youtube.com/watch?v=XhmM8HZj7aU
    */


    // The number of samples required in order to output.
    auto bufferSamples = ioBuffer.getNumSamples();
    auto readPointers = ioBuffer.getArrayOfReadPointers();    // Pointers to the input channels

    rbBuffer1.makeCopyOf(ioBuffer);
    rbBuffer2.makeCopyOf(ioBuffer);
    rbBuffer3.makeCopyOf(ioBuffer);
    auto writePointers1 = rbBuffer1.getArrayOfWritePointers();  // Pointers to the output channels
    auto writePointers2 = rbBuffer2.getArrayOfWritePointers();  // Pointers to the output channels
    auto writePointers3 = rbBuffer3.getArrayOfWritePointers();  // Pointers to the output channels

    rubberband1->process(readPointers, bufferSamples, false);
    rubberband2->process(readPointers, bufferSamples, false);
    rubberband3->process(readPointers, bufferSamples, false);
    auto samplesAvailable1 = rubberband1->available();
    auto samplesAvailable2 = rubberband2->available();
    auto samplesAvailable3 = rubberband3->available();

    DBG("-----------------------------------------------------------------------");
    if (samplesAvailable1 >= bufferSamples && samplesAvailable2 >= bufferSamples && samplesAvailable3 >= bufferSamples)
    {
        DBG("samplesAvailable1: " << samplesAvailable1 << "samplesAvailable2: " << samplesAvailable2
            << "samplesAvailable3: " << samplesAvailable3);
        rubberband1->retrieve(writePointers1, bufferSamples);
        rubberband2->retrieve(writePointers2, bufferSamples);
        rubberband3->retrieve(writePointers3, bufferSamples);

        DBG("  -- retrieve --" << "   bufferSamples: " << bufferSamples);
        samplesAvailable1 = rubberband1->available();
        samplesAvailable2 = rubberband2->available();
        samplesAvailable3 = rubberband3->available();

        DBG("samplesAvailable1: " << samplesAvailable1 << "samplesAvailable2: " << samplesAvailable2
            << "samplesAvailable3: " << samplesAvailable3);

        ioBuffer.applyGain(0, 0, bufferSamples, 0.00);
        ioBuffer.addFrom(0, 0, rbBuffer1, 0, 0, bufferSamples, 0.35);
        ioBuffer.addFrom(0, 0, rbBuffer2, 0, 0, bufferSamples, 0.45);
        ioBuffer.addFrom(0, 0, rbBuffer3, 0, 0, bufferSamples, 0.20);
        ioBuffer.applyGain(0, 0, bufferSamples, 2.0);
    }
    else
    {
        DBG("NOT ENOUGH SAMPLES AVAILABLE");
    }
}

//==============================================================================
bool PrismusAudioProcessor::hasEditor() const
{
    return true; // (change this to false if you choose to not supply an editor)
}

juce::AudioProcessorEditor* PrismusAudioProcessor::createEditor()
{
    return new PrismusAudioProcessorEditor (*this);
}

//==============================================================================
void PrismusAudioProcessor::getStateInformation (juce::MemoryBlock& destData)
{
    // You should use this method to store your parameters in the memory block.
    // You could do that either as raw data, or use the XML or ValueTree classes
    // as intermediaries to make it easy to save and load complex data.
}

void PrismusAudioProcessor::setStateInformation (const void* data, int sizeInBytes)
{
    // You should use this method to restore your parameters from this memory block,
    // whose contents will have been created by the getStateInformation() call.
}

//==============================================================================
// This creates new instances of the plugin..
juce::AudioProcessor* JUCE_CALLTYPE createPluginFilter()
{
    return new PrismusAudioProcessor();
}
