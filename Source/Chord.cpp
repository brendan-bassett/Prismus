/*
  ==============================================================================

    Chord.cpp
    Created: 13 Dec 2023 6:20:53pm
    Author:  bbass

  ==============================================================================
*/

#include "Chord.h"

using namespace std;

Chord::Chord(int midiRoot) // Default to Middle C (C3)
{
	updateRoot(midiRoot);
	updateMidiMap();
}

//------------------------------------------------------------------------------

void Chord::addNote(int midiNoteNumber)
{
	rwLock.enterWrite();

	if (midiNoteNumber <= Chord::rootLimit)
	{
		cout << "Midi note number " << midiNoteNumber
			<< " Is too low to be a note. It must be above " << rootLimit
			<< " or else it is a root. Cannot add it to the chord." << endl;
		return;
	}

	Interval interval = midiMap[midiNoteNumber].copy();
	Note note = Note(midiNoteNumber, interval);

	// Ensure the note is not already in the chord before adding it.
	for (auto n = noteList.begin(); n != noteList.end(); ++n)
		if (n->midiNoteNumber == midiNoteNumber) {
			cout << "Note number " << midiNoteNumber << " is already in the chord! Cannot add it.";
			return;
		}

	noteList.push_front(note);

	rwLock.exitWrite();
}

void Chord::removeNote(int midiNoteNumber)
{
	rwLock.enterWrite();

	if (midiNoteNumber <= Chord::rootLimit)
	{
		cout << "Midi note number " << midiNoteNumber
			<< " Is too low to be a note. It must be above " << rootLimit
			<< " or else it is a root. Cannot remove it from the chord." << endl;
		return;
	}

	// Find the corresponding note in the chord, then remove it.
	for (auto n = noteList.begin(); n != noteList.end(); ++n)
		if (n->midiNoteNumber == midiNoteNumber) {
			noteList.remove(*n);
			return;
		}

	cout << "Note number " << midiNoteNumber << " does not exist in the chord! Cannot remove it.";

	rwLock.exitWrite();
}

float Chord::getRootRelP()
{
	rwLock.enterRead();

	float rrp = root.interval.getRelP();

	rwLock.exitRead();

	return rrp;
}

list<float>& Chord::getNotesRelP()
{
	rwLock.enterRead();

	float rrp = getRootRelP();
	list<float> relPList;

	for (auto n = noteList.begin(); n != noteList.end(); ++n)
	{
		float nrp = rrp + n->interval.getRelP();
		relPList.push_back(nrp);
	}

	rwLock.exitRead();

	return relPList;
}

void Chord::updateRoot(int r)
{
	rwLock.enterWrite();

	if (r > Chord::rootLimit)
	{
		cout << "Root number " << r
			<< " Is too high to be a valid root. Must be MIDI note number 47 (B2) or below." << endl;
		return;
	}

	root = Note(r + 24, Interval(1, 1)); // Translate indicated root up two octaves.
	updateMidiMap();

	rwLock.exitWrite();
}

//-----------------------------------------------------------------------------

void Chord::updateMidiMap()
{
	// Map the intervals as indicated by the standard interval map, centered around the root.

	// Find the starting point of the root in the 
	int translation = root.midiNoteNumber - rootLimit + 1;
	int octaves = -(translation / 12);
	translation = translation % 12;

	// Above the root
	for (int i = rootLimit + 1, int m = translation; i <= 88; ++i, ++m)
	{
		if (m == 12)
		{
			m = 0;
			++octaves;
		}

		Interval interval = standardIntervalMap[m].copy();
		interval.translateOctaves(octaves);
		midiMap[i] = interval;
	}

}

//-----------------------------------------------------------------------------

map<int, Interval> initStandardIntervalMap()
{
	map<int, Interval> m;
	m[0] = Interval(1, 1); // Unison
	m[1] = Interval(0, 0); // Minor Second - NULL INTERVAL
	m[2] = Interval(9, 8); // Major Second
	m[3] = Interval(6, 5); // Minor Third
	m[4] = Interval(5, 4); // Major Third
	m[5] = Interval(4, 3); // Perfect Fourth
	m[6] = Interval(0, 0); // Tritone - NULL INTERVAL
	m[7] = Interval(3, 2); // Perfect Fifth
	m[8] = Interval(8, 5); // Minor Sixth
	m[9] = Interval(5, 3); // Major Sixth
	m[10] = Interval(9, 5); // Minor Seventh
	m[11] = Interval(0, 0); // Major Seventh - NULL INTERVAL

	return m;
}
map<int, Interval> Chord::standardIntervalMap(initStandardIntervalMap());
