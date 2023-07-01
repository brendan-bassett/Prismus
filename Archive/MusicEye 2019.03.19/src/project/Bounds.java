package project;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import gui.CustomGraphics;
import gui.ScoreWindow;
import util.L;


abstract public class Bounds {

	public final ScoreItem	scoreItem;
	public ScoreDrawable	scoreDrawable;
	public boolean			isVisible	= false;

	protected final ScoreWindow	window;

	private boolean			hasFocus	= false;


	protected Bounds(ScoreItem _scoreItem, ScoreWindow _window) {
		scoreItem = _scoreItem;
		window = _window;

		checkVisibility();
		resize(false);
	}


	abstract protected boolean checkVisibility();


	public boolean contains(Point _point) {
		if (scoreDrawable != null) {
			return scoreDrawable.contains(_point);
		} else {
			L.q("Bounds.contains()", "called while scoreDrawable == null");
			return false;
		}
	}

	public void draw(CustomGraphics _draw) {
		if (scoreDrawable != null) {
			scoreDrawable.draw(_draw);
		}
	}

	public void createPopupMenu(MouseEvent _event) {
		if (scoreDrawable != null) {
			scoreDrawable.createPopupMenu(_event);
		} else {
			L.q("Bounds.createPopupMenu()", "called while scoreDrawable == null");
		}
	}

	public void mouseExited(Point _point) {
		if (scoreDrawable != null) {
			scoreDrawable.mouseExited(_point);
		} else {
			L.q("Bounds.mouseExited()", "called while scoreDrawable == null");
		}
	}

	public void mouseEntered(Point _point) {
		if (scoreDrawable != null) {
			scoreDrawable.mouseEntered(_point);
		} else {
			L.q("Bounds.mouseEntered()", "called while scoreDrawable == null");
		}
	}

	public void keyPressed(KeyEvent _event) {
		if (scoreDrawable != null) {
			scoreDrawable.keyPressed(_event);
		} else {
			L.q("Bounds.keyPressed()", "called while scoreDrawable == null");
		}
	}

	public void keyReleased(KeyEvent _event) {
		if (scoreDrawable != null) {
			scoreDrawable.keyReleased(_event);
		} else {
			L.q("Bounds.keyReleased()", "called while scoreDrawable == null");
		}
	}

	public void mouseDragged(Point _point) {
		if (scoreDrawable != null) {
			scoreDrawable.mouseDragged(_point);
		} else {
			L.q("Bounds.mouseDragged()", "called while scoreDrawable == null");
		}
	}

	public void mouseMoved(Point _point) {
		if (scoreDrawable != null) {
			scoreDrawable.mouseMoved(_point);
		} else {
			L.q("Bounds.mouseMoved()", "called while scoreDrawable == null");
		}
	}

	public ActionItem mousePressed(Point _point) {
		if (scoreDrawable != null) {
			return scoreDrawable.mousePressed(_point);
		} else {
			L.q("Bounds.mousePressed()", "called while scoreDrawable == null");
			return null;
		}
	}

	public void mouseReleased(Point _point) {
		if (scoreDrawable != null) {
			scoreDrawable.mouseReleased(_point);
		} else {
			L.q("Bounds.mouseReleased()", "called while scoreDrawable == null");
		}
	}

	public void move(int _dX, int _dY) {
		isVisible = checkVisibility();
		if (isVisible) {
			if (scoreDrawable == null) {
				scoreDrawable = scoreItem.createDrawable(window);
				scoreDrawable.setHasFocus(hasFocus);

			} else {
				scoreDrawable.move(_dX, _dY);
			}
		} else {
			if (scoreDrawable != null) {
				scoreDrawable = null;
			}
		}
	}

	public void resize(boolean _remakeDrawable) {
		isVisible = checkVisibility();
		if (isVisible) {
			if (scoreDrawable == null || _remakeDrawable) {
				scoreDrawable = scoreItem.createDrawable(window);
				scoreDrawable.setHasFocus(hasFocus);
			} else {
				scoreDrawable.resize();
			}

		} else {
			if (scoreDrawable != null) {
				scoreDrawable = null;
			}
		}
	}

	public void setHasFocus(boolean _hasFocus) {
		hasFocus = _hasFocus;
		if (scoreDrawable != null) {
			scoreDrawable.setHasFocus(_hasFocus);
		} else {
			L.q("Bounds.setHasFocus()", "called while scoreDrawable == null");
		}
	}

}
