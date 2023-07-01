package project;

import file.FILE;
import file.FileElement;
import util.CustomException;
import util.K;
import util.L;

/**
 * Created by Brendan on 3/31/2015.
 */
public class Ratio implements FILE.RATIO_F {

	static public Ratio read(FileElement _e) throws CustomException {
		int octaves = _e.get(OCTAVES);
		int numerator = _e.get(NUMERATOR);
		int denominator = _e.get(DENOMINATOR);

		return new Ratio(octaves, numerator, denominator);
	}

	static public Ratio multiply(Ratio _ratio1, Ratio _ratio2) {
		return new Ratio(	(_ratio1.octaves + _ratio2.octaves),
							(_ratio1.numerator * _ratio2.numerator),
							(_ratio1.denominator * _ratio2.denominator));
	}


	public final int		denominator;
	public final boolean	isUnison;
	public final int		numerator;
	public final int		octaves;
	public final float		relP;


	public Ratio() {
		this(1, 1);
	}

	public Ratio(int _numerator, int _denominator) {
		this(0, _numerator, _denominator);
	}

	public Ratio(int _octaves, int _numerator, int _denominator) {
		int newOctaves = _octaves;
		int newNumerator = _numerator;
		int newDenominator = _denominator;

		if (newNumerator < newDenominator) {
			int n = newNumerator;
			int d = newDenominator;
			newDenominator = n;
			newNumerator = d;
		}

		float decimal = (float) newNumerator / (float) newDenominator;
		if (decimal >= 2) {
			newOctaves = (int) decimal;
			newDenominator = newDenominator * (int) Math.round(Math.pow(2, newOctaves));
		}

		int n = -1;
		int d = -1;

		for (int i = 1; i <= newDenominator; i++) {
			if (((newNumerator % i) == 0) && ((newDenominator % i) == 0)) {
				n = (newNumerator / i);
				d = (newDenominator / i);
			}
		}

		if ((n / d) == (newNumerator / newDenominator)) {
			octaves = newOctaves;
			numerator = n;
			denominator = d;
		} else {
			octaves = 0;
			numerator = 0;
			denominator = 0;
			L.e("Ratio()",
				"fraction simplification failed! _numerator==" + _numerator + " denominator==" + denominator + " n==" + n + " d==" + d);
		}

		if (((float) numerator / (float) denominator) == 1 && octaves == 0) {
			relP = 0;
		} else {
			relP = K.relPFromRatio(numerator, denominator, octaves);
		}

		if (relP == 0) {
			isUnison = true;
		} else {
			isUnison = false;
		}
	}


	public String toString() {
		if (numerator == 1 && denominator == 1) {
			return "T";
		} else if (numerator > 9 || denominator > 9) {
			return numerator + ":" + denominator;
		} else {
			return Integer.toString(numerator) + Integer.toString(denominator);
		}
	}

	public void write(FileElement _d) throws CustomException {
		_d.add(OCTAVES, octaves);
		_d.add(NUMERATOR, numerator);
		_d.add(DENOMINATOR, denominator);
	}

}
