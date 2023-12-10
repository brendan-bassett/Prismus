/*
  ==============================================================================

    NoteStructure.cpp
    Created: 5 Dec 2023 7:29:12pm
    Author:  bbass

  ==============================================================================
*/

#include "NoteStructure.h"
#include <forward_list> as list;
#include <map>;

using namespace std;

//==============================================================================

Interval::Interval(int numerator = 1, int denominator = 1, int octaves = 0)
{
	// Ensure the musical ratio is "proper". That means the numerator divided by the denominator is less than 2, and 
	// greater than or equal to one. Also the numerator and denominator must be expressed as the lowest integers 
	// possible. The trick here is to retain the appropriate octave translation while doing so.

	// A musical interval ratio cannot be 0. Cannot divide by zero. This is considered a "null" interval.
	if (numerator == 0 || denominator == 0)
	{
		numerator = 0;
		denominator = 0;
		octaves = 0;
		return;
	}


	// Make sure the musical ratio is greater than 1. Keep increasing by an octave until this is true, 
	// adjusting the octave translation accordingly.
	while (numerator < denominator)
	{
		numerator = numerator * 2;
		octaves--;
	}

	// Reduce the musical ratio by some number of octaves until it is between unison 
	// and 1 octave (1 <= relative pitch < 2).
	// Here we multiply the denominator by a power of 2 instead of dividing the numerator by a power of 2. 
	// This prevents any possible loss of information due to int truncation.

	float decimal = (float)numerator / (float)denominator;

	while (decimal > 2) {
		denominator = denominator * 2;
		octaves++;
		decimal = (float)numerator / (float)denominator;
	}

	// Make sure that the numerator and denominator are expressed as the smallest integers possible. Divide both the 
	// numerator and denominator by any common factor.
	while (true) {
		for (int i = (numerator / 2 + 1); i >= 2; i--) {
			if (numerator % i == 0 && denominator % i == 0)
			{
				numerator = numerator / i;
				denominator = denominator / i;
				continue;   // A common factor was found and applied. Try to find a new common factor.
			}
		}
		break;  // No new common factor was found. Stop iterating.
	}

	// Save the formatted ratio & octave information as member variables.
	this->numerator = numerator;
	this->denominator = denominator;
	this->octaves = octaves;

	// The "size" of the interval in log2 relative pitch space is used frequently with rendering. Save this calculation.
	if (((float)numerator / (float)denominator) == 1 && octaves == 0) {
		relP = 0;
	}
	else {
		relP = log2((float)numerator / (float)denominator);
	}

}

//------------------------------------------------------------------------------

string Interval::asString() const
{
	if (numerator == 0)
		return "NULL";

	return "ratio==" + to_string(numerator) + ":" + to_string(denominator) + "  octaves==" + to_string(octaves);
}

string Interval::asShorthand(bool unisonIsTonic) const
{
	if (numerator == 0)
		return "NULL";

	if (isUnison())
	{
		if (unisonIsTonic == true)
			return "T";

		return "U";
	}

	if (numerator == 3 && denominator == 2)
		return "3";

	if (numerator == 4 && denominator == 3)
		return "4";

	if (numerator == 5 && denominator == 4)
		return "5";

	if (numerator == 6 && denominator == 5)
		return "6";

	if (numerator == 7 && denominator == 4)
		return "7";

	if (numerator < 10 && denominator < 10)
		return to_string(numerator) + to_string(denominator);

	string ratio = to_string(numerator) + ":" + to_string(denominator);

	if (ratio.length() > 8)
		return "X";

	return ratio;
}

Interval Interval::copy()
{
	return Interval(numerator, denominator, octaves);
}

bool Interval::isUnison(bool considerOctaves) const
{
	if (considerOctaves)
	{
		if (relP == 0 && octaves == 0)
			return true;

		return false;
	}

	if (relP == 0)
		return true;

	return false;
}

void Interval::removeOctaves()
{
	octaves = 0;
}

void Interval::translateOctaves(int translation)
{
	octaves = octaves + translation;
}

int Interval::getDenominator() const
{
	return denominator;
}

int Interval::getNumerator() const
{
	return numerator;
}

int Interval::getOctaves() const
{
	return octaves;
}

float Interval::getAbsPMultiplier(bool includeOctaves) const
{
	return ((float)numerator / (float)denominator) + (octaves * 2);
}

float Interval::getRelP(bool includeOctaves) const
{
	if (includeOctaves)
		return relP + octaves;

	return relP;
}

//==============================================================================

CompoundInterval::CompoundInterval(Interval parentInterval, Interval childInterval)
	: parentInterval(parentInterval), childInterval(childInterval)
{}

//------------------------------------------------------------------------------

string CompoundInterval::asString() const
{
	return parentInterval.asShorthand() + "|" + childInterval.asShorthand() + "   octaves:"
		+ to_string(parentInterval.getOctaves() + childInterval.getOctaves());
}

string CompoundInterval::asShorthand() const
{
	return parentInterval.asShorthand() + "|" + childInterval.asShorthand();
}

bool CompoundInterval::isUnison(bool considerOctaves) const
{
	return (parentInterval.isUnison(considerOctaves) && childInterval.isUnison(considerOctaves));
}

//==============================================================================

Note::Note(int midiNoteNumber = 60, Interval i = Interval()) : midiNoteNumber(midiNoteNumber), interval(interval)
{}

//==============================================================================

Chord::Chord(int midiRoot) // Default to Middle C (C3)
{
	updateRoot(midiRoot);
	updateMidiMap();
}

//------------------------------------------------------------------------------

void Chord::addNote(int midiNoteNumber)
{
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
}

void Chord::removeNote(int midiNoteNumber)
{
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
}

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

void Chord::updateRoot(int r)
{
	if (r > Chord::rootLimit)
	{
		cout << "Root number " << r
			<< " Is too high to be a valid root. Must be MIDI note number 47 (B2) or below." << endl;
		return;
	}

	root = Note(r + 24, Interval(1, 1)); // Translate indicated root up two octaves.
	updateMidiMap();
}

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

