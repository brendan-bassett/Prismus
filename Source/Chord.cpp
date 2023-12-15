/*
  =============================================================================

    Chord.cpp
    Created: 13 Dec 2023 6:20:53pm
    Author:  Brendan D Bassett

  =============================================================================
*/

#include <JuceHeader.h>

#include "Chord.h"

// PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

Chord::Chord(int midiRoot) // Default to Middle C (C3)
{
	updateRoot(midiRoot);
	updateMidiMap();
}

//-- Instance Functions -------------------------------------------------------

void Chord::addNote(int midiNoteNumber)
{
	rwLock.enterWrite();

	if (midiNoteNumber <= Chord::rootLimit)
	{
		std::cout << "Midi note number " << midiNoteNumber
			<< " Is too low to be a note. It must be above " << rootLimit
			<< " or else it is a root. Cannot add it to the chord." << std::endl;
		return;
	}

	Interval interval{ midiMap[midiNoteNumber] };
	Note note{ interval, midiNoteNumber };

	// Ensure the note is not already in the chord before adding it.
	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
		if (n->midiNoteNumber == midiNoteNumber) {
			std::cout << "Note number " << midiNoteNumber << " is already in the chord! Cannot add it.";
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
		std::cout << "Midi note number " << midiNoteNumber
			<< " Is too low to be a note. It must be above " << rootLimit
			<< " or else it is a root. Cannot remove it from the chord." << std::endl;
		return;
	}

	// Find the corresponding note in the chord, then remove it.
	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
	{
		if (n->midiNoteNumber == midiNoteNumber) {
			noteList.remove(*n);
			return;
		}
	}

	std::cout << "Note number " << midiNoteNumber << " does not exist in the chord! Cannot remove it.";

	rwLock.exitWrite();
}

float Chord::getRootRelP()
{
	rwLock.enterRead();

	float rrp{ root.interval.getRelP() };

	rwLock.exitRead();

	return rrp;
}

std::forward_list<int> Chord::getMidiNoteNumbers()
{
	rwLock.enterRead();

	std::forward_list<int> midiNoteList;

	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
		midiNoteList.push_front(n->midiNoteNumber);

	rwLock.exitRead();

	return midiNoteList;
}

std::forward_list<float> Chord::getNotesRelP()
{
	rwLock.enterRead();

	float rrp{ getRootRelP() };
	std::forward_list<float> relPList;

	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
	{
		float nrp{ rrp + n->interval.getRelP() };
		relPList.push_front(nrp);
	}

	rwLock.exitRead();

	return relPList;
}

std::forward_list<float> Chord::getNotesMultipliers()
{
	rwLock.enterRead();

	std::forward_list<float> multiList;

	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
	{
		float nrp{ n->interval.getAbsPMultiplier() };
		multiList.push_front(nrp);
	}

	rwLock.exitRead();

	return multiList;
}

bool Chord::hasNote(int midiNoteNumber)
{
	for (auto n{ noteList.begin() }; n != noteList.end(); ++n)
		if (n->midiNoteNumber == midiNoteNumber) return true;

	return false;
}

void Chord::updateRoot(int r)
{
	rwLock.enterWrite();

	if (r > Chord::rootLimit)
	{
		std::cout << "Root number " << r
			<< " Is too high to be a valid root. Must be MIDI note number 47 (B2) or below." << std::endl;
		return;
	}

	root = Note(Interval(1, 1), r + 24); // Translate indicated root up two octaves.
	updateMidiMap();

	rwLock.exitWrite();
}

// PRIVATE
//=============================================================================

//-- Instance Functions -------------------------------------------------------

void Chord::updateMidiMap()
{
	// Map the intervals as indicated by the standard interval map, centered around the root.

	// DO NOT READ/WRITE LOCK. Private function is only accessed from within member functions already implementing lock.

	int translation{ root.midiNoteNumber - rootLimit + 1 };
	int octaves{ - (translation / 12)};
	translation %= 12;			// Remove octaves from translation.

	for (int i{ rootLimit + 1 }, m{ translation }; i <= 88; ++i, ++m)
	{
		if (m == 12)
		{
			m = 0;
			++octaves;
		}

		Interval interval{ standardIntervalMap[m]};		// This automatically copies the Interval
		interval.translateOctaves(octaves);
		midiMap[i] = interval;
	}
}

// NON-MEMBER
//=============================================================================

std::map<int, Interval>& initStandardIntervalMap()
{
	std::map<int, Interval> m;

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

std::map<int, Interval>& Chord::standardIntervalMap(initStandardIntervalMap());
