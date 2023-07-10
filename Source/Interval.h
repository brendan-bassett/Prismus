/*
  ==============================================================================

    Interval.h
    Created: 6 Jul 2023 1:10:26pm
    Author:  Brendan Bassett

  ==============================================================================
*/

#pragma once

using std::string;

/// @brief A musical interval expressed as a relative intonation interval.

class Interval
{

public:
    //=======================================================================================================================

    Interval(int numerator = 1, int denominator = 1, int octaves = 0);

    // We use getters and setters for these variables because each variablecannot be a const. However we do not want 
    // outside functions to be able to change them. In the case of relP, it is used so frequently in rendering that we do not 
    // want to calculate it every time due to the complexity of log2 math operations. The value is saved, then accessed 
    // through a getter function.

    int getDenominator() const;
    int getNumerator() const;
    int getOctaves() const;
    float getRelP() const;

    //-----------------------------------------------------------------------------------------------------------------------

    bool isUnison() const;
    string asString() const;
    
    //-----------------------------------------------------------------------------------------------------------------------



private:
    //=======================================================================================================================

    //-----------------------------------------------------------------------------------------------------------------------

    // These variables cannot be const because they are set after the initialize list within the constructor.

    int		denominator {};
    int		numerator {};
    int		octaves {};
    float	relP = 0.0f;

};
