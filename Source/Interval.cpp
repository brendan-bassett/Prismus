/*
  ==============================================================================

	Interval.cpp
	Created: 6 Jul 2023 1:10:26pm
	Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <cmath>
#include <iostream>

#include "Interval.h"
#include <string>

using namespace std;

//== CONSTRUCTORS ======================================================================================================

Interval::Interval(int numerator, int denominator, int octaves)
{
	// Ensure the musical ratio is "proper". That means the numerator divided by the denominator is less than 2, and 
	// greater than or equal to one. Also the numerator and denominator must be expressed as the lowest integers 
	// possible. The trick here is to retain the appropriate octave translation while doing so.

	// A musical interval ratio cannot be 0.
	if (numerator == 0)
	{
		cout << "ERROR: Interval(): A musical interval ratio cannot be 0." << endl;
		numerator = 1;
	}

	// A musical interval ratio cannot divide by 0.
	if (denominator == 0)
	{
		cout << "ERROR: Interval(): A musical interval ratio cannot divide by 0." << endl;
		denominator = 1;
	}


	// Make sure the musical ratio is greater than 1. Keep increasing by an octave until this is true, 
	// adjusting the octave translation accordingly.
	while (numerator < denominator) {
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

//== PUBLIC METHODS ====================================================================================================

string Interval::asString() const
{
	return "ratio==" + to_string(numerator) + ":" + to_string(denominator) + "  octaves==" + to_string(octaves);
}

string Interval::asShorthand(bool unisonIsTonic) const
{
	if (isUnison())
	{
		if (unisonIsTonic == true)
		{
			return "T";
		}
		return "U";
	}

	if (numerator == 3 && denominator == 2)
	{
		return "3";
	}

	if (numerator == 4 && denominator == 3)
	{
		return "4";
	}

	if (numerator == 5 && denominator == 4)
	{
		return "5";
	}

	if (numerator == 6 && denominator == 5)
	{
		return "6";
	}

	if (numerator == 7 && denominator == 4)
	{
		return "7";
	}

	if (numerator < 10 && denominator < 10)
	{
		return to_string(numerator) + to_string(denominator);
	}

	string ratio = to_string(numerator) + ":" + to_string(denominator);

	if (ratio.length() > 8)
	{
		return "X";
	}

	return ratio;
}

bool Interval::isUnison(bool considerOctaves) const
{
	if (considerOctaves)
	{
		if (relP == 0 && octaves == 0)
		{
			return true;
		}
		return false;
	}

	if (relP == 0)
	{
		return true;
	}
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

//-- GETTERS -----------------------------------------------------------------------------------------------------------

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

float Interval::getRelP(bool includeOctaves) const
{
	if (includeOctaves)
	{
		return relP + octaves;
	}
	else
	{
		return relP;
	}
}

//== PRIVATE METHODS ===================================================================================================

