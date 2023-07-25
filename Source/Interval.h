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
    //==================================================================================================================

    Interval(int numerator = 1, int denominator = 1, int octaves = 0);

    // We use getters and setters for these variables because each variable cannot be a const. They cannot be const 
    // because they are set after the initialize list within the constructor. However we do not want outside functions 
    // to be able to change them. In the case of relP, it is used so frequently in rendering that we do not 
    // want to calculate it every time due to the complexity of log2 math operations. The value is saved, then accessed 
    // through a getter function.

    //------------------------------------------------------------------------------------------------------------------

    /// @brief Create a string representation of the interval.
    /// @return A string representation of the interval.
    string asString() const;

    /// @brief Determine whether the interval is unison.
    /// @param considerOctaves Whether to consider octaves. If TRUE, the function will return TRUE only if relP and 
    ///                        octaves are both 0. Defaults to TRUE.
    /// @return Whether the interval is unison.
    bool isUnison(bool considerOctaves = true) const;

    /// @brief Set octaves for this interval to 0.
    void removeOctaves();

    /// @brief  Translate the interval by a number of octaves.
    /// @param octaves The number of octaves to translate.
    void translateOctaves(int octaves);

    //-- GETTERS -------------------------------------------------------------------------------------------------------

    /// @brief Simple getter method for the denominator.
    /// @return The denominator.
    int getDenominator() const;

    /// @brief Simple getter method for the numerator.
    /// @return The numerator.
    int getNumerator() const;

    /// @brief Simple getter method for the octaves.
    /// @return The octaves.
    int getOctaves() const;

    /// @brief Simple getter method for the relative pitch.
    /// @param includeOctaves Whether to include octaves in the calculation of the relative pitch distance. 
    ///                       Defaults to TRUE.
    /// @return The relative pitch.
    float getRelP(bool includeOctaves = true) const;


private:
    //==================================================================================================================

    //------------------------------------------------------------------------------------------------------------------

    // These variables cannot be const because they are set after the initialize list within the constructor.

    /// @brief The denominator of the musical ratio.
    int		denominator {};

    /// @brief The numerator of the musical ratio.
    int		numerator {};

    /// @brief The number of octaves to translate the ratio by.
    int		octaves {};

    /// @brief The relative pitch distance of the musical ratio. DOES NOT INCLUDE OCTAVES, because they can be added 
    ///        with simple math when calculating the overall relative pitch of the interval.
    float	relP = 0.0f;

};
