package project;

import java.awt.Point;
import java.awt.event.KeyEvent;

import gui.CustomGraphics;
import gui.ScoreWindow;

public abstract class ActionItem {

	protected Project	project;
	protected ScoreWindow	window;


	public ActionItem(ScoreWindow _window) {
		window = _window;
		project = window.project;
	}

	abstract public void cancel();

	abstract public void mouseDragged(Point _point);

	abstract public void mouseReleased(Point _point);


	public void draw(CustomGraphics _g) {}

	public void keyPressed(KeyEvent _event) {}

	public void keyReleased(KeyEvent _event) {}

	public void mouseMoved(Point _point) {}

	public void mousePressed(Point _point) {}

	public void move(int _dX, int _dY) {}

	public void resize() {}

}
