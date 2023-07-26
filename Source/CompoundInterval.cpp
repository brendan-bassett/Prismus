/*
  ==============================================================================

    CompoundInterval.cpp
    Created: 25 Jul 2023 7:48:24pm
    Author:  Brendan Bassett

  ==============================================================================
*/

#include "CompoundInterval.h"

CompoundInterval::CompoundInterval(Interval parentInterval, Interval childInterval)
    : parentInterval{ parentInterval }, childInterval{ childInterval }
{

}