/*
  ==============================================================================

    Interval.cpp
    Created: 6 Jul 2023 1:10:26pm
    Author:  Brendan Bassett

  ==============================================================================
*/

#include <cmath>
#include <iostream>

#include "Interval.h"
#include <string>

using namespace std;

Interval::Interval(int numerator, int denominator, int octaves) 
{
	// Ensure the musical ratio is "proper". That means the numerator divided by the denominator is less than 2, and greater
	// than or equal to one. Also the numerator and denominator must be expressed as the lowest integers possible. The trick
	// here is to retain the appropriate octave translation while doing so.
	
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

	// Combine the given octave information with the given ratio. We need to apply the octaves to EITHER the numerator 
	// or denominator to avoid dividing an int by an int, resulting in truncated numbers.
	if (octaves > 0) {
		numerator = numerator * pow(2, octaves);
	}
	else if (octaves < 0)
	{
		denominator = denominator * pow(2, -octaves);
	}

	octaves = 0;
	// Extract all extra octave translation information from this combined ratio.

	float decimal = (float)numerator / (float)denominator;

	int extraOctaves = 0;

	if (decimal >= 2)
	{
		int extraOctaves = (int)log2(decimal);	// This cast from float to int always rounds down to the nearest int.
		numerator = numerator / pow(2, extraOctaves);
	}

	octaves = octaves + extraOctaves;

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

	// The "size" of the interval in log2 relative pitch space is used frequently with rendering. Calculate and save this.
	if (((float)numerator / (float)denominator) == 1 && octaves == 0) {
		relP = 0;
	}
	else {
		relP = (float)(log2((float)numerator / (float)denominator) + (float)octaves);
	}
	
}

bool Interval::isUnison()
{
	if (relP == 0) {
		return true;
	}
	return false;
}

string Interval::asString()
{
	return "ratio==" + to_string(numerator) + "/" + to_string(denominator) + "  octaves==" + to_string(octaves);
}