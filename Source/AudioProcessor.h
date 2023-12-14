/*
  =============================================================================

    AudioProcessor.h
    Created: 16 Aug 2023 10:00:12pm
    Author:  Brendan D Bassett

  =============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "rubberband/RubberBandStretcher.h"
#include "MidiProcessor.h"

using RubberBand::RubberBandStretcher;


class AudioProcessor  : public juce::AudioProcessor,
                         public juce::ChangeListener
                    #if JucePlugin_Enable_ARA
                       , public juce::AudioProcessorARAExtension
                    #endif
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    AudioProcessor();
    ~AudioProcessor() override;
    
	//-- Instance Functions ---------------------------------------------------

    void changeListenerCallback(juce::ChangeBroadcaster* source) override
;
    void prepareToPlay (double sampleRate, int samplesPerBlock) override;
    void processBlock (juce::AudioBuffer<float>&, juce::MidiBuffer&) override;
    void releaseResources() override;
    void updateChord(Chord& chord);

    //-------------------------------------------------------------------------

    bool acceptsMidi() const override;
    void changeProgramName(int index, const juce::String& newName) override;
    juce::AudioProcessorEditor* createEditor() override;
    int getCurrentProgram() override;
    const juce::String getName() const override;
    int getNumPrograms() override;
    const juce::String getProgramName(int index) override;
    double getTailLengthSeconds() const override;
    bool hasEditor() const override;
    bool isMidiEffect() const override;
    bool producesMidi() const override;
    void setCurrentProgram(int index) override;
    void setMidiProcessor(MidiProcessor* midiProcessor);

    #ifndef JucePlugin_PreferredChannelConfigurations
    bool isBusesLayoutSupported(const BusesLayout& layouts) const override;
    #endif

    //-------------------------------------------------------------------------
    // Save State Functions

    void getStateInformation (juce::MemoryBlock& destData) override;
    void setStateInformation (const void* data, int sizeInBytes) override;

private:
    //=========================================================================

    //-- Instance Variables ---------------------------------------------------

    MidiProcessor* midiProcessor;

    std::map<int, RubberBandStretcher*> rubberbandMap;
    std::map<int, juce::AudioBuffer<float>> rbBufferMap;
    std::forward_list<float* const*> writePointersList;
    std::forward_list<int> samplesAvailableList;

    //-- Static Variables -----------------------------------------------------

    const static int maxPolyphony = 5;

    //-------------------------------------------------------------------------

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (AudioProcessor)
};
