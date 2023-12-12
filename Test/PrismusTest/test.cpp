#include "pch.h"

#include "../../Source/NoteStructure.h"
#include "../../Source/Interval.cpp"

// Midiprocessor Tests
//-----------------------------------------------------------------------------

TEST(Note, EqualNoteTest)
{
	Note note1 = Note();
	Note note2 = Note(60);

	ASSERT_EQ(note1, note2);
}

// Interval Tests
//-----------------------------------------------------------------------------
TEST(Interval, InvalidRatio1) {
	Interval interval = Interval(0, 1);	// Not a valid interval. Should be changed to default values

	ASSERT_EQ(interval.getNumerator(), 1);
	ASSERT_EQ(interval.getDenominator(), 1);
	ASSERT_EQ(interval.getOctaves(), 0);
	ASSERT_FLOAT_EQ(interval.getRelP(), 0.0f);
}

TEST(Interval, InvalidRatio2) {
	Interval interval = Interval(1, 0);	// Not a valid interval. Should be changed to default values

	ASSERT_EQ(interval.getNumerator(), 1);
	ASSERT_EQ(interval.getDenominator(), 1);
	ASSERT_EQ(interval.getOctaves(), 0);
	ASSERT_FLOAT_EQ(interval.getRelP(), 0.0f);
}

TEST(Interval, DefaultUnisonRatio) {
	Interval interval = Interval();

	ASSERT_EQ(interval.getNumerator(), 1);
	ASSERT_EQ(interval.getDenominator(), 1);
	ASSERT_EQ(interval.getOctaves(), 0);
	ASSERT_FLOAT_EQ(interval.getRelP(), 0.0f);
}

TEST(Interval, ProperRatio) {
	Interval interval = Interval(5, 4);

	ASSERT_EQ(interval.getNumerator(), 5);
	ASSERT_EQ(interval.getDenominator(), 4);
	ASSERT_EQ(interval.getOctaves(), 0);
	ASSERT_FLOAT_EQ(interval.getRelP(), 0.3219280948873623f);
}

TEST(Interval, ProperRatioIncreasedOctave) {
	Interval interval = Interval(5, 4, 2);

	ASSERT_EQ(interval.getNumerator(), 5);
	ASSERT_EQ(interval.getDenominator(), 4);
	ASSERT_EQ(interval.getOctaves(), 2);
	ASSERT_FLOAT_EQ(interval.getRelP(), 2.3219280948873623f);
}

TEST(Interval, ProperRatioDecreasedOctave) {
	Interval interval = Interval(5, 4, -2);

	ASSERT_EQ(interval.getNumerator(), 5);
	ASSERT_EQ(interval.getDenominator(), 4);
	ASSERT_EQ(interval.getOctaves(), -2);
	ASSERT_FLOAT_EQ(interval.getRelP(), -1.6780719051126376f);
}

TEST(Interval, ImproperRatioLessThan1) {
	Interval interval = Interval(3, 4);

	ASSERT_EQ(interval.getNumerator(), 3);
	ASSERT_EQ(interval.getDenominator(), 2);
	ASSERT_EQ(interval.getOctaves(), -1);
	ASSERT_FLOAT_EQ(interval.getRelP(), -0.4150374992788438f);
}

TEST(Interval, ImproperRatioGreaterThan1) {
	Interval interval = Interval(8, 3);

	ASSERT_EQ(interval.getNumerator(), 4);
	ASSERT_EQ(interval.getDenominator(), 3);
	ASSERT_EQ(interval.getOctaves(), 1);
	ASSERT_FLOAT_EQ(interval.getRelP(), 1.4150374992788437f);
}

int main(int argc, char** argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}