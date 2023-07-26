/*
  ==============================================================================

    CompoundInterval.h
    Created: 25 Jul 2023 7:48:24pm
    Author:  Brendan Bassett

  ==============================================================================
*/

#pragma once

#include <string>

#include "Interval.h"

using std::string;

class CompoundInterval
{

public:
    //==================================================================================================================

    CompoundInterval(Interval parentInterval, Interval childInterval);

    Interval const childInterval {};
    Interval const parentInterval {};

};