/*
  ==============================================================================

    This file contains the basic framework code for a JUCE plugin processor.

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>
#include "rubberband/RubberBandStretcher.h"

using RubberBand::RubberBandStretcher;

//==============================================================================
/**
*/
class PrismusAudioProcessor  : public juce::AudioProcessor
                            #if JucePlugin_Enable_ARA
                             , public juce::AudioProcessorARAExtension
                            #endif
{
public:
    //==============================================================================
    PrismusAudioProcessor();
    ~PrismusAudioProcessor() override;

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

    std::unique_ptr<RubberBandStretcher> rubberband1;
    std::unique_ptr<RubberBandStretcher> rubberband2;
    std::unique_ptr<RubberBandStretcher> rubberband3;

    juce::AudioBuffer<float> rbBuffer1;
    juce::AudioBuffer<float> rbBuffer2;
    juce::AudioBuffer<float> rbBuffer3;

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (PrismusAudioProcessor)
};
