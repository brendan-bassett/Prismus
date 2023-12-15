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

using std::forward_list;
using std::map;

class Chord
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    Chord(int midiRoot = 60); // Default to Middle C (C3)

    //-- Instance Functions ---------------------------------------------------

    void addNote(int midiNoteNumber);

    forward_list<float> getNotesRelP();

    forward_list<int> getMidiNoteNumbers();
    
    forward_list<float> getNotesMultipliers();
    
    float getRootRelP();
    
    bool hasNote(int midiNoteNumber);
    
    void removeNote(int midiNoteNumber);
    
    void updateRoot(int r);

    //-- Static Variables -----------------------------------------------------

    const static int rootLimit = 48; // MIDI notes above B2 cannot be used as a root.

private:
    //=========================================================================

    //-- Subclasses & Enums ---------------------------------------------------

    struct Note
    {
        Note(Interval i, int mnn = 60) : interval(i), midiNoteNumber(mnn)
        {}

        bool operator == (const Note& n)
        {
            return midiNoteNumber == n.midiNoteNumber;
        }

        Interval interval;
        int midiNoteNumber;
    };

    //-- Instance Functions ---------------------------------------------------

    void updateMidiMap();

    map<int, Interval> midiMap;
    forward_list<Note> noteList;
    Note root{ Interval() };

    const juce::ReadWriteLock rwLock;

    //-- Static Variables -----------------------------------------------------

    static std::map<int, Interval>& standardIntervalMap;     // Populated in initStandardIntervalMap(). Cannot be const.

};
