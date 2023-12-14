/*
  ==============================================================================

    Interval.h
    Created: 13 Dec 2023 6:20:47pm
    Author:  Brendan D Bassett

  ==============================================================================
*/

#pragma once

#include <JuceHeader.h>


class Interval
{

public:
    //=========================================================================

    //-- Constructors & Destructors -------------------------------------------

    Interval(int numerator = 1, int denominator = 1, int octaves = 0);

    //-- Instance Functions ---------------------------------------------------

    std::string asShorthand(bool unisonIsTonic = false) const;
    std::string asString() const;
    bool isUnison(bool considerOctaves = true) const;
    Interval removeOctaves() const;
    Interval translateOctaves(int octaves) const;

    //-------------------------------------------------------------------------
    // Getters

    int getDenominator() const;
    int getNumerator() const;
    int getOctaves() const;
    float getAbsPMultiplier(bool includeOctaves = true) const;
    float getRelP(bool includeOctaves = true) const;

private:
    //=========================================================================

    //-- Instance Variables ---------------------------------------------------


    // We use getters and setters for these variables because each variable cannot be a const. They cannot be const 
    // because they are set after the initialize list within the constructor. However we do not want outside functions 
    // to be able to change them. In the case of relP, it is used so frequently in rendering that we do not 
    // want to calculate it every time due to the complexity of log2 math operations. The value is saved, then accessed 
    // through a getter function.

    int		denominator{};      // Treat as const
    int		numerator{};        // Treat as const
    int		octaves{};          // Treat as const


    // The relative pitch distance of the musical ratio. This saves on complex logarithmic calculations. 
    // DOES NOT INCLUDE OCTAVES, because they can be with simple math when calculating the overall relative 
    // pitch of the interval.

    float	relP = 0.0f;        // Treat as const

};
