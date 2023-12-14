/*
  =============================================================================

    Chord.h
    Created: 13 Dec 2023 6:20:53pm
    Author:  Brendan D Bassett

  =============================================================================
*/

#pragma once

#include <JuceHeader.h>

#include <forward_list>

#include "Interval.h"
#include "Note.h"

class Chord
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    Chord(int midiRoot = 60); // Default to Middle C (C3)

    //-- Instance Functions ---------------------------------------------------

    void addNote(int midiNoteNumber);
    std::forward_list<float> getNotesRelP();
    std::forward_list<int> getMidiNoteNumbers();
    std::forward_list<float> getNotesMultipliers();
    float getRootRelP();
    bool hasNote(int midiNoteNumber);
    void removeNote(int midiNoteNumber);
    void updateRoot(int r);

    //-- Static Variables -----------------------------------------------------

    const static int rootLimit = 48; // MIDI notes above B2 cannot be used as a root.

private:
    //=========================================================================

    //-- Instance Functions ---------------------------------------------------

    void updateMidiMap();

    std::map<int, Interval> midiMap;
    std::forward_list<Note> noteList;
    Note root;
    juce::ReadWriteLock rwLock;

    //-- Static Variables -----------------------------------------------------

    static std::map<int, Interval>& standardIntervalMap;     // Populated in initStandardIntervalMap(). Cannot be const.

};
