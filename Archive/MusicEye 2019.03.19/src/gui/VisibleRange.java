package gui;


public class VisibleRange {

	private final ScoreWindow	window;

	public float			leftT		= 0;
	public float			topRelP		= 0;
	public float			rightT		= 0;
	public float			bottomRelP	= 0;


	VisibleRange(ScoreWindow _window) {
		window = _window;
	}


	void update() {
		leftT = window.timeFromScreenX(window.boundsFrame.left);
		topRelP = window.relPFromScreenY(window.boundsFrame.top);
		rightT = window.timeFromScreenX(window.boundsFrame.right);
		bottomRelP = window.relPFromScreenY(window.boundsFrame.bottom);
	}

	public boolean intersects(float _t) {
		if (_t > leftT && _t < rightT) {
			return true;
		}
		return false;
	}

	public boolean intersects(float _t, float _relP) {
		if (_t >= leftT && _t <= rightT && _relP <= topRelP && _relP >= bottomRelP) {
			return true;
		}
		return false;
	}

	public boolean intersects(float _leftT, float _relP, float _rightT) {
		if (_relP > bottomRelP && _relP < topRelP) {
			if (_leftT < leftT && _rightT > rightT) {
				return true;
			} else if (_leftT > leftT && _leftT < rightT) {
				return true;
			} else if (_rightT > leftT && _rightT < rightT) {
				return true;
			}
		}
		return false;
	}

	public boolean intersects(float _leftT, float _topRelP, float _rightT, float _bottomRelP) {
		if (_leftT > leftT && _leftT < rightT) {
			if (_topRelP > bottomRelP && _topRelP < topRelP) {
				return true;
			} else if (_bottomRelP > bottomRelP && _bottomRelP < topRelP) {
				return true;
			} else if (_bottomRelP <= bottomRelP && _topRelP >= topRelP) {
				return true;
			}
		} else if (_rightT > leftT && _rightT < rightT) {
			if (_topRelP > bottomRelP && _topRelP < topRelP) {
				return true;
			} else if (_bottomRelP > bottomRelP && _bottomRelP < topRelP) {
				return true;
			} else if (_bottomRelP <= bottomRelP && _topRelP >= topRelP) {
				return true;
			}
		} else if (_topRelP > bottomRelP && _topRelP < topRelP) {
			if (_leftT > leftT && _leftT < rightT) {
				return true;
			} else if (_rightT > leftT && _rightT < rightT) {
				return true;
			} else if (_leftT <= leftT && _rightT >= rightT) {
				return true;
			}
		} else if (_bottomRelP > bottomRelP && _bottomRelP < topRelP) {
			if (_leftT > leftT && _leftT < rightT) {
				return true;
			} else if (_rightT > leftT && _rightT < rightT) {
				return true;
			} else if (_leftT <= leftT && _rightT >= rightT) {
				return true;
			}
		} else if (_leftT <= leftT && _topRelP >= topRelP && _rightT >= rightT && _bottomRelP <= bottomRelP) {
			return true;
		}
		return false;
	}

	public String toString() {
		return ("[leftT=" + leftT + " topRelP=" + topRelP + " rightT=" + rightT + " bottomRelP=" + bottomRelP + "]");
	}

}
