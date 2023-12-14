/*
  ==============================================================================

    Note.h
    Created: 13 Dec 2023 6:20:41pm
    Author:  bbass

  ==============================================================================
*/

#pragma once

#include "Interval.h";

class Note
{

public:

    Note(int midiNoteNumber = 60, Interval i = Interval());

    bool operator == (const Note& n)
    {
        return midiNoteNumber == n.midiNoteNumber;
    }

    int midiNoteNumber;
    Interval interval;

};
