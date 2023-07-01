package gui;

import java.awt.Point;

import file.FILE;
import file.FileElement;
import util.CustomException;

public class Rect implements FILE.RECT_F {


	static public Rect read(FileElement _e) throws CustomException {
		int left = _e.get(LEFT);
		int top = _e.get(TOP);
		int right = _e.get(RIGHT);
		int bottom = _e.get(BOTTOM);

		return new Rect(left, top, right, bottom);
	}


	public int	left;
	public int	top;
	public int	right;
	public int	bottom;

	public int	width;
	public int	height;


	public Rect(Point _point1, Point _point2) {
		if (_point1.x < _point2.x) {
			left = _point1.x;
			right = _point2.x;
		} else {
			left = _point2.x;
			right = _point1.x;
		}

		if (_point1.y > _point2.y) {
			top = _point1.y;
			bottom = _point2.y;
		} else {
			top = _point2.y;
			bottom = _point1.y;
		}

		width = (right - left);
		height = (top - bottom);
	}

	public Rect(int _left, int _top, int _right, int _bottom) {
		if (_left > _right) {
			left = _right;
			right = _left;
		} else {
			left = _left;
			right = _right;
		}

		if (_top < _bottom) {
			top = _bottom;
			bottom = _top;
		} else {
			top = _top;
			bottom = _bottom;
		}

		width = (right - left);
		height = (top - bottom);
	}

	public Rect() {
		this(0, 0, 0, 0);
	}


	public int centerX() {
		return ((left + right) / 2);
	}

	public int centerY() {
		return ((top + bottom) / 2);
	}

	public boolean contains(Point _point) {
		return contains(_point.x, _point.y);
	}

	public boolean contains(int _x, int _y) {
		boolean b = false;
		if (_x >= left && _x <= right) {
			if (_y <= top && _y >= bottom) {
				b = true;
			}
		}
		return b;
	}

	public boolean contains(int _left, int _top, int _right, int _bottom) {
		boolean b = false;
		if (_left >= left && _top <= top && _right <= right && _bottom >= bottom) {
			b = true;
		}
		return b;
	}

	public Rect copy() {
		return new Rect(left, top, right, bottom);
	}

	public boolean intersects(Rect _rect) {
		return intersects(_rect.left, _rect.top, _rect.right, _rect.bottom);
	}

	public boolean intersects(int _left, int _top, int _right, int _bottom) {
		if (_left > left && _left < right) {
			if (_top > bottom && _top < top) {
				return true;
			} else if (_bottom > bottom && _bottom < top) {
				return true;
			} else if (_bottom <= bottom && _top >= top) {
				return true;
			}
		} else if (_right > left && _right < right) {
			if (_top > bottom && _top < top) {
				return true;
			} else if (_bottom > bottom && _bottom < top) {
				return true;
			} else if (_bottom <= bottom && _top >= top) {
				return true;
			}
		} else if (_top > bottom && _top < top) {
			if (_left > left && _left < right) {
				return true;
			} else if (_right > left && _right < right) {
				return true;
			} else if (_left <= left && _right >= right) {
				return true;
			}
		} else if (_bottom > bottom && _bottom < top) {
			if (_left > left && _left < right) {
				return true;
			} else if (_right > left && _right < right) {
				return true;
			} else if (_left <= left && _right >= right) {
				return true;
			}
		} else if (_left <= left && _top >= top && _right >= right && _bottom <= bottom) {
			return true;
		}
		return false;
	}

	public void move(int _dX, int _dY) {
		left = (left + _dX);
		top = (top + _dY);
		right = (right + _dX);
		bottom = (bottom + _dY);
	}

	public void set(Rect _rect) {
		set(_rect.left, _rect.top, _rect.right, _rect.bottom);
	}

	public void set(int _left, int _top, int _right, int _bottom) {
		if (_left <= _right) {
			left = _left;
			right = _right;
		} else {
			left = _right;
			right = _left;
		}

		if (_top >= _bottom) {
			top = _top;
			bottom = _bottom;
		} else {
			top = _bottom;
			bottom = _top;
		}

		width = (_right - _left);
		height = (_top - _bottom);
	}

	@Override
	public String toString() {
		return ("[l=" + left + " t=" + top + " r=" + right + " b=" + bottom + "]");
	}

	public void write(FileElement _e) throws CustomException {
		_e.add(LEFT, left);
		_e.add(TOP, top);
		_e.add(RIGHT, right);
		_e.add(BOTTOM, bottom);
	}

}
