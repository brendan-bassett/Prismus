/*
  ==============================================================================

    RubberbandNote.cpp
    Created: 14 Dec 2023 6:54:29pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#include <JuceHeader.h>

#include "RubberbandNote.h"

// PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

RubberbandNote::RubberbandNote(RubberBandStretcher& rbs, AudioBuffer<float>& ab, float gain)
    : rubberband(rbs), audioBuffer(ab), gain(gain)
{ }

//-- Instance Functions -------------------------------------------------------

void RubberbandNote::adjustGain(float gainTarget)
{
    lock.enterWrite();

    if (gainTarget > 1.f) {
        DBG("ERROR: gain cannot be more than 1");
        return;
    }

    gain = gainTarget;

    lock.exitWrite();
}

int RubberbandNote::getMidiNoteNumber() const
{
    lock.enterRead();
    int mnn = midiNoteNumber;
    lock.exitRead();

    return mnn;
}

int RubberbandNote::getSamplesAvailable() const
{
    lock.enterRead();
    int sa = samplesAvailable;
    lock.exitRead();

    return sa;
}

bool RubberbandNote::isActive() const
{
    lock.enterRead();
    bool a = active;
    lock.exitRead();

    return a;
}

void RubberbandNote::noteOn(int mnn)
{
    lock.enterWrite();

    active = true;
    midiNoteNumber = mnn;

    lock.exitWrite();
}

void RubberbandNote::noteOff()
{
    lock.enterWrite();

    active = false;
    midiNoteNumber = 0;

    lock.exitWrite();
}

void RubberbandNote::output(AudioBuffer<float>& ioBuffer, int bufferSamples)
{
    lock.enterWrite();

    if (!isActive)
    {
        DBG("ERROR: output() called on inactive RubberbandNote");
        return;
    }

    ioBuffer.addFrom(0, 0, audioBuffer, 0, 0, bufferSamples, gain);

    lock.exitWrite();
}

void RubberbandNote::process(AudioBuffer<float>& ioBuffer, int bufferSamples)
{
    lock.enterWrite();

    if (!isActive)
    {
        DBG("ERROR: process() called on inactive RubberbandNote");
        return;
    }

    audioBuffer.makeCopyOf(ioBuffer);
    auto readPointers{ ioBuffer.getArrayOfReadPointers() };
    writePointers = audioBuffer.getArrayOfWritePointers();

    rubberband.process(readPointers, bufferSamples, false);

    samplesAvailable = rubberband.available();

    lock.exitWrite();
}

void RubberbandNote::retrieve(int bufferSamples)
{
    lock.enterWrite();

    if (!isActive)
    {
        DBG("ERROR: retrieve() called on inactive RubberbandNote");
        return;
    }

    if (writePointers == NULL)
    {
        DBG("ERROR: writePointers == NULL. Must call process() before retrieve()");
        return;
    }

    rubberband.retrieve(writePointers, bufferSamples);

    samplesAvailable = rubberband.available();

    writePointers = NULL;

    lock.exitWrite();
}