/*
  ==============================================================================

    NoteStructure.h
    Created: 5 Dec 2023 7:29:12pm
    Author:  bbass

  ==============================================================================
*/

#include <JuceHeader.h>
#include <forward_list> as list;
#include <map>;

using namespace std;

//==============================================================================
//------------------------------------------------------------------------------

class Interval
{

public:
    //==================================================================================================================

    Interval(int numerator, int denominator, int octaves);

    //------------------------------------------------------------------------------------------------------------------

    string asString() const;

    string asShorthand(bool unisonIsTonic = false) const;

    Interval copy();

    bool isUnison(bool considerOctaves = true) const;

    /// @brief Set octaves for this interval to 0.
    void removeOctaves();

    void translateOctaves(int octaves);

    //-- GETTERS -------------------------------------------------------------------------------------------------------

    int getDenominator() const;

    int getNumerator() const;

    int getOctaves() const;

    float getAbsPMultiplier(bool includeOctaves = true) const;

    float getRelP(bool includeOctaves = true) const;


private:
    //==================================================================================================================

    // We use getters and setters for these variables because each variable cannot be a const. They cannot be const 
    // because they are set after the initialize list within the constructor. However we do not want outside functions 
    // to be able to change them. In the case of relP, it is used so frequently in rendering that we do not 
    // want to calculate it every time due to the complexity of log2 math operations. The value is saved, then accessed 
    // through a getter function.

    int		denominator{};

    int		numerator{};

    int		octaves{};

    /// @brief The relative pitch distance of the musical ratio. DOES NOT INCLUDE OCTAVES, because they can be added 
    ///        with simple math when calculating the overall relative pitch of the interval.
    float	relP = 0.0f;

};

class CompoundInterval
{

public:
    //==================================================================================================================

    CompoundInterval(Interval parentInterval, Interval childInterval);

    string asString() const;

    string asShorthand() const;

    bool isUnison(bool considerOctaves = true) const;

    Interval parentInterval;
    Interval childInterval;

private:
    //==================================================================================================================

};

//==============================================================================
//------------------------------------------------------------------------------

class Note
{

public:

    Note(int noteNumber, Interval interval);

    bool operator == (const Note& n)
    {
        return midiNoteNumber == n.midiNoteNumber;
    }

    int midiNoteNumber;
    Interval interval;

};

//==============================================================================
//------------------------------------------------------------------------------

class Chord
{

public:

    Chord(int midiRoot = 60);

    void addNote(int midiNoteNumber);

    void removeNote(int midiNoteNumber);

    void updateRoot(int r);

    list<Note> noteList;

    Note root;
    int rootLimit = 48; // MIDI notes above B2 cannot be used as a root.

private:

    void updateMidiMap();

    map<int, Interval> midiMap;

    static map<int, Interval> standardIntervalMap;

};
