package util;

import project.Project;

abstract public class K {

	static public final float	CENTS_PER_SEMITONE		= 0.000833f;				//100 cents is equal to one semitone
	static public final int		SECONDS_PER_MINUTE		= 60;
	static public final int		SEMITONES_PER_OCTAVE	= 12;
	static public final float	SEMITONE_FRACTION		= 1f / SEMITONES_PER_OCTAVE;
	static public final int		TWO_POW_7				= 128;
	static public final int		TWO_POW_13				= 8192;

	static private final float	LOG_10_OF_2				= (float) Math.log10(2);
	static private final float	MS_PER_S				= 1000;


//	TODO:Change all relP terms to cents

	static public float hzFromRelP(Project _project, float _relP) {
		return ((float) (Math.pow(2, _relP)) * _project.harmonicCenter);
	}

	static public float relPFromHz(Project _project, float _hz) {
		return (float) ((Math.log10(_hz / _project.harmonicCenter)) / LOG_10_OF_2);
	}

	static public float relPFromHz(float _hz1, float _hz2) {
		return (float) (Math.log10(_hz1 / _hz2) / LOG_10_OF_2);
	}

	static public float relPFromRatio(int _numerator, int _denominator, int _octaves) {
		return (float) ((Math.log10((float) _numerator / (float) _denominator)) / LOG_10_OF_2) + (float) _octaves;
	}

	static public float timeFromBeats(float _beats, float _tempo) {
		float t;
		t = (_beats / _tempo * SECONDS_PER_MINUTE);
		return t;
	}

}
