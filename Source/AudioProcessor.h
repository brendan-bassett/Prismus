/*
  ==============================================================================

    This file contains the basic framework code for a JUCE plugin processor.

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>
#include <forward_list> as list;
#include "rubberband/RubberBandStretcher.h"

using RubberBand::RubberBandStretcher;
using namespace std;

//==============================================================================
/**
*/
class AudioProcessor  : public juce::AudioProcessor,
                         public juce::ChangeListener
                    #if JucePlugin_Enable_ARA
                       , public juce::AudioProcessorARAExtension
                    #endif
{
public:
    //==============================================================================
    AudioProcessor();
    ~AudioProcessor() override;

    //==============================================================================
    void changeListenerCallback(juce::ChangeBroadcaster* source) override;

    //==============================================================================
    void prepareToPlay (double sampleRate, int samplesPerBlock) override;
    void releaseResources() override;

   #ifndef JucePlugin_PreferredChannelConfigurations
    bool isBusesLayoutSupported (const BusesLayout& layouts) const override;
   #endif

    void processBlock (juce::AudioBuffer<float>&, juce::MidiBuffer&) override;

    //==============================================================================
    juce::AudioProcessorEditor* createEditor() override;
    bool hasEditor() const override;

    //==============================================================================
    const juce::String getName() const override;

    bool acceptsMidi() const override;
    bool producesMidi() const override;
    bool isMidiEffect() const override;
    double getTailLengthSeconds() const override;

    //==============================================================================
    int getNumPrograms() override;
    int getCurrentProgram() override;
    void setCurrentProgram (int index) override;
    const juce::String getProgramName (int index) override;
    void changeProgramName (int index, const juce::String& newName) override;

    //==============================================================================
    void getStateInformation (juce::MemoryBlock& destData) override;
    void setStateInformation (const void* data, int sizeInBytes) override;

private:
    //==============================================================================

    const int maxNotes = 5;

    list<RubberBandStretcher> rubberbandList;
    list<juce::AudioBuffer<float>> rbBufferList;
    list<float* const*> writePointersList;
    list<int> samplesAvailableList;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (AudioProcessor)
};
