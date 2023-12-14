/*
  ==============================================================================

    Chord.h
    Created: 13 Dec 2023 6:20:53pm
    Author:  bbass

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include <forward_list> as list
#include <map>

#include "Interval.h"
#include "Note.h"


class Chord
{

public:

    Chord(int midiRoot = 60); // Default to Middle C (C3)

    void addNote(int midiNoteNumber);

    float getRootRelP();

    list<float> getNotesRelP();

    void removeNote(int midiNoteNumber);

    void updateRoot(int r);

    int rootLimit = 48; // MIDI notes above B2 cannot be used as a root.

private:

    void updateMidiMap();

    map<int, Interval> midiMap;

    static map<int, Interval> standardIntervalMap;

    list<Note> noteList;

    Note root;

};
