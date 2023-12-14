/*
  ==============================================================================

    AudioProcessor.cpp
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "AudioProcessor.h"
#include "PluginEditor.h"
#include "MidiProcessor.h"

#include <src/common/RingBuffer.h>
#include "rubberband/RubberBandStretcher.h"

using RubberBand::RubberBandStretcher;
using RubberBand::RingBuffer;


//PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

AudioProcessor::AudioProcessor()
    #ifndef JucePlugin_PreferredChannelConfigurations
     : juce::AudioProcessor (BusesProperties()
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

AudioProcessor::~AudioProcessor()
{
}

//-- Instance Functions -------------------------------------------------------

void AudioProcessor::changeListenerCallback(juce::ChangeBroadcaster* source)
{
    if (midiProcessor == nullptr) return;
    if (source == nullptr) return;

    if (typeid(source) == typeid(MidiProcessor))
        updateChord(midiProcessor->getActiveChord());
}

void AudioProcessor::prepareToPlay (double sampleRate, int samplesPerBlock)
{

    DBG("TOTAL NUMBER INPUT CHANNELS: " << getTotalNumInputChannels());
    DBG("TOTAL NUMBER OUTPUT CHANNELS: " << getTotalNumOutputChannels());

    for (int i{ 0 }; i < maxPolyphony; ++i)
    {
        // It is assumed that the lowest notes on the keyboard are reserved for indicating the root of a chord. So we
        // map the unused buffers to these lowest notes.
        rbBufferMap[i] = juce::AudioBuffer<float>(getTotalNumInputChannels(), samplesPerBlock);

        rubberbandMap[i] = &RubberBandStretcher(sampleRate,
            getTotalNumOutputChannels(),
            RubberBandStretcher::PresetOption::DefaultOptions | RubberBandStretcher::Option::OptionProcessRealTime,
            1.0,
            1.0);
    }
}

void AudioProcessor::processBlock (juce::AudioBuffer<float>& ioBuffer, juce::MidiBuffer& midiMessages)
{
    int bufferSamples{ ioBuffer.getNumSamples() };
    auto readPointers{ ioBuffer.getArrayOfReadPointers() };    // Pointers to the input channels

    for (auto it{ rbBufferMap.begin() }; it != rbBufferMap.end(); ++it)
    {
        it->second.makeCopyOf(ioBuffer);
        writePointersList.push_front(it->second.getArrayOfWritePointers());
    }

    for (auto it{ rubberbandMap.begin() }; it != rubberbandMap.end(); ++it)
    {
        RubberBandStretcher* rubberband{ it->second };
        rubberband->process(readPointers, bufferSamples, false);
        samplesAvailableList.push_front(rubberband->available());
    }

    DBG("/n-----------------------------------------------------------------------/n");

    bool proceedWithBlock{ true };
    int i{ 1 };
    for (auto sa{ samplesAvailableList.begin() }; sa != samplesAvailableList.end(); ++sa)
    {
        if (*sa < bufferSamples)
        {
            DBG("Rubberband " << i << " samples available: " << *sa << "     (NOT ENOUGH SAMPLES AVAILABLE)");
            proceedWithBlock = false;
        }
        else
        {
            DBG("Rubberband " << i << " samples available: " << *sa);
        }

        ++i;
    }

    if (proceedWithBlock)
    {
        DBG("/nRetrieve " << bufferSamples << " samples from each rubberband instance.");

        auto wp{ writePointersList.begin() };
        for (auto it = rubberbandMap.begin(); it != rubberbandMap.end(); ++it)
        {
            RubberBandStretcher* rubberband{ it->second };
            rubberband->retrieve(*wp, bufferSamples);
            ++wp;
        }

        ioBuffer.applyGain(0, 0, bufferSamples, 0.00);

        for (auto it = rbBufferMap.begin(); it != rbBufferMap.end(); ++it)
        {
            it->second.addFrom(0, 0, it->second, 0, 0, bufferSamples, 1/(float)maxPolyphony);
        }

        ioBuffer.applyGain(0, 0, bufferSamples, 2.0);  // TODO implement master gain slider to control this.
    }
    else
    {
        DBG("/nNOT ENOUGH SAMPLES AVAILABLE. SKIP THIS BLOCK");
    }

    writePointersList.clear();
    samplesAvailableList.clear();
}

void AudioProcessor::releaseResources()
{
    // When playback stops, you can use this as an opportunity to free up any
    // spare memory, etc.
}

void AudioProcessor::updateChord(Chord& chord)
{
    std::forward_list<int> midiNoteNumber { chord.getMidiNoteNumbers() };
    for (auto it{ rubberbandMap.begin() }; it != rubberbandMap.end(); ++it)
    {
        // TODO: Separate inactive instances of RubberBand from active ones
        // TODO: Combine RubberBand instances and their corresponding AudioBuffers and samplesAvailable
    }
}

//-----------------------------------------------------------------------------

bool AudioProcessor::acceptsMidi() const
{
    #if JucePlugin_WantsMidiInput
    return true;
    #else
    return false;
    #endif
}

void AudioProcessor::changeProgramName(int index, const juce::String& newName)
{
}

juce::AudioProcessorEditor* AudioProcessor::createEditor()
{
    return new PluginEditor(*this);
}

int AudioProcessor::getCurrentProgram()
{
    return 0;
}

const juce::String AudioProcessor::getName() const
{
    return JucePlugin_Name;
}

int AudioProcessor::getNumPrograms()
{
    return 1;   // NB: some hosts don't cope very well if you tell them there are 0 programs,
    // so this should be at least 1, even if you're not really implementing programs.
}

const juce::String AudioProcessor::getProgramName(int index)
{
    return {};
}

double AudioProcessor::getTailLengthSeconds() const
{
    return 0.0;
}

bool AudioProcessor::hasEditor() const
{
    return true;
}

bool AudioProcessor::isMidiEffect() const
{
    #if JucePlugin_IsMidiEffect
    return true;
    #else
    return false;
    #endif
}

bool AudioProcessor::producesMidi() const
{
    #if JucePlugin_ProducesMidiOutput
    return true;
    #else
    return false;
    #endif
}

void AudioProcessor::setCurrentProgram(int index)
{
}

void AudioProcessor::setMidiProcessor(MidiProcessor* mp)
{
    midiProcessor = mp;
}

#ifndef JucePlugin_PreferredChannelConfigurations
bool AudioProcessor::isBusesLayoutSupported(const BusesLayout& layouts) const
{
    #if JucePlugin_IsMidiEffect
    juce::ignoreUnused(layouts);
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

//-----------------------------------------------------------------------------
// Save State Functions

void AudioProcessor::getStateInformation (juce::MemoryBlock& destData)
{
    // You should use this method to store your parameters in the memory block.
    // You could do that either as raw data, or use the XML or ValueTree classes
    // as intermediaries to make it easy to save and load complex data.
}

void AudioProcessor::setStateInformation (const void* data, int sizeInBytes)
{
    // You should use this method to restore your parameters from this memory block,
    // whose contents will have been created by the getStateInformation() call.
}

// NON-MEMBER
//==============================================================================

// "MAIN" function. Initializes the VST plugin processor
juce::AudioProcessor* JUCE_CALLTYPE createPluginFilter()
{
    return new AudioProcessor();
}
