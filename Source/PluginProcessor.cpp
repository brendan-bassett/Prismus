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
    // "Dynamic memory": Memory that is allocated while the app is running.
    rubberband = std::make_unique<RubberBandStretcher>(sampleRate,
        getTotalNumOutputChannels(),
        RubberBandStretcher::PresetOption::DefaultOptions,
        0.5,
        0.5);

    rubberband->reset();
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

void PrismusAudioProcessor::processBlock (juce::AudioBuffer<float>& buffer, juce::MidiBuffer& midiMessages)
{
    juce::ScopedNoDenormals noDenormals;
    auto totalNumInputChannels  = getTotalNumInputChannels();
    auto totalNumOutputChannels = getTotalNumOutputChannels();

    // In case we have more outputs than inputs, this code clears any output
    // channels that didn't contain input data, (because these aren't
    // guaranteed to be empty - they may contain garbage).
    // This is here to avoid people getting screaming feedback
    // when they first compile a plugin, but obviously you don't need to keep
    // this code if your algorithm always overwrites all the output channels.
    for (auto i = totalNumInputChannels; i < totalNumOutputChannels; ++i)
        buffer.clear (i, 0, buffer.getNumSamples());

    // This is the place where you'd normally do the guts of your plugin's
    // audio processing...
    // Make sure to reset the state if your inner loop is processing
    // the samples and the outer loop is handling the channels.
    // Alternatively, you can process the samples with the channels
    // interleaved by keeping the same state.
    for (int channel = 0; channel < totalNumInputChannels; ++channel)
    {
        auto* channelData = buffer.getWritePointer (channel);

        // ..do something to the data...
    }


    /*
    * FROM "Livestream - Implementing a TimeStretching Library (RubberBand)"  by The Audio Programmer
    * https://www.youtube.com/watch?v=XhmM8HZj7aU
    */


    auto readPointers = buffer.getArrayOfReadPointers();
    auto writePointers = buffer.getArrayOfWritePointers();

    rubberband->process(readPointers, buffer.getNumSamples(), false);

    auto samplesAvailable = rubberband->available();

    //DBG("Samples available: " << samplesAvailableFromStretcher);

    if (buffer.getNumSamples() < samplesAvailable)
    {
        auto rbOutput = rubberband->retrieve(writePointers, buffer.getNumSamples());


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
