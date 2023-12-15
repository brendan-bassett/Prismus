/*
  ==============================================================================

    RubberbandNote.h
    Created: 14 Dec 2023 6:54:28pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include "rubberband/RubberBandStretcher.h"

using juce::AudioBuffer;

using RubberBand::RubberBandStretcher;


class RubberbandNote
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    RubberbandNote(RubberBandStretcher& rbs, AudioBuffer<float>& ab, float gain);

    //-- Instance Functions ---------------------------------------------------

    void adjustGain(float gainTarget);

    int getMidiNoteNumber() const;

    int getSamplesAvailable() const;

    bool isActive() const;    // Use this getter method so "active" can remain private and internally managed.

    void noteOn(int mnn);

    void noteOff();

    void output(AudioBuffer<float>& ioBuffer, int bufferSamples);

    void process(AudioBuffer<float>& ioBuffer, int bufferSamples);

    void retrieve(int bufferSamples);


private:
    //=========================================================================

    //-- Instance Variables ---------------------------------------------------



    bool active{ false };
    float gain{ 0.f };
    int midiNoteNumber{ 0 };
    int samplesAvailable{ 0 };

    AudioBuffer<float>& audioBuffer;
    RubberBandStretcher& rubberband;
    float* const* writePointers = NULL;

    const juce::ReadWriteLock lock;
};
