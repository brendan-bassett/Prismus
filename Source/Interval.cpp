/*
  ==============================================================================

    Interval.cpp
    Created: 13 Dec 2023 6:20:47pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#include "Interval.h"

//PUBLIC
//=============================================================================

//-- Constructors & Destructors -----------------------------------------------

Interval::Interval(int numerator, int denominator, int octaves)
{
	// Ensure the musical ratio is "proper". That means the numerator divided by the denominator is less than 2, and 
	// greater than or equal to one. Also the numerator and denominator must be expressed as the lowest integers 
	// possible. The trick here is to retain the appropriate octave translation while doing so.

	// Allow name shadowing here as the parameter names are usually changed, then 
	// instantiate the instance variables at the end of this function.

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

	float decimal{ (float)numerator / (float)denominator };

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

	// The use of "this" here overrides the name shadowing that we opted to use in this one function.

	this->numerator = numerator;
	this->denominator = denominator;
	this->octaves = octaves;

	// The "size" of the interval in log2 relative pitch space is used frequently with rendering. Save this calculation.
	
	if (((float)numerator / (float)denominator) == 1 && octaves == 0) {
		// This prevents a resulting relP of minute float value, when it should be 0.
		relP = 0;
	}
	else {
		relP = log2((float)numerator / (float)denominator);
	}
}

//-- Instance Functions -------------------------------------------------------

std::string Interval::asString() const
{
	if (numerator == 0) return "NULL";

	return "ratio==" + std::to_string(numerator) + ":" + std::to_string(denominator) + "  octaves==" + std::to_string(octaves);
}

std::string Interval::asShorthand(bool unisonIsTonic) const
{
	if (numerator == 0) return "NULL";

	if (isUnison())
	{
		if (unisonIsTonic == true) return "T";

		return "U";
	}

	if (numerator == 3 && denominator == 2) return "3";

	if (numerator == 4 && denominator == 3) return "4";

	if (numerator == 5 && denominator == 4) return "5";

	if (numerator == 6 && denominator == 5) return "6";

	if (numerator == 7 && denominator == 4) return "7";

	if (numerator < 10 && denominator < 10)
		return std::to_string(numerator) + std::to_string(denominator);

	std::string ratio { std::to_string(numerator) + ":" + std::to_string(denominator) };

	if (ratio.length() > 8) return ".:.";

	return ratio;
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

Interval Interval::removeOctaves() const
{
	return Interval(numerator, denominator, octaves);
}

Interval Interval::translateOctaves(int translation) const
{
	return Interval(numerator, denominator, octaves + translation);
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
