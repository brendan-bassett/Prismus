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
#include "RubberbandNote.h"

using std::forward_list;

using juce::String;
using juce::AudioBuffer;

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

    void processBlock (AudioBuffer<float>&, juce::MidiBuffer&) override;

    void releaseResources() override;

    void updateChord(Chord& chord);

    //-------------------------------------------------------------------------

    bool acceptsMidi() const override;

    void changeProgramName(int index, const juce::String& newName) override;

    juce::AudioProcessorEditor* createEditor() override;

    int getCurrentProgram() override;

    const String getName() const override;

    int getNumPrograms() override;

    const String getProgramName(int index) override;

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

    forward_list<RubberbandNote> activeRbNotes;
    forward_list<RubberbandNote> inactiveRbNotes;
    forward_list<float* const*> writePointersList;

    //-- Static Variables -----------------------------------------------------

    const static int maxActiveRubberbands{ 5 };
    const static int maxInactiveRubberbands{ 2 };

    //-------------------------------------------------------------------------

    JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR (AudioProcessor)
};
