package gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import project.Project;
import util.L;


public class KeyManager implements KeyEventDispatcher {

	static private ArrayList<Integer> list = new ArrayList<Integer>();


	static public boolean has(Integer _integer) {
		for (Integer i : list) {
			if (i.equals(_integer)) {
				return true;
			}
		}
		return false;
	}


	public Project project;


	public KeyManager(Project _project) {
		project = _project;
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent _event) {
		int keycode = _event.getKeyCode();
		int id = _event.getID();

		if (id == KeyEvent.KEY_PRESSED) {
			if (!has(keycode)) {
				list.add(keycode);
			}
			if (project.focusedWindow != null) {
				project.focusedWindow.windowListener.keyPressed(_event);
			}

		} else if (id == KeyEvent.KEY_RELEASED) {
			boolean hasKey = false;
			for (Integer i : list) {
				if (i.equals(keycode)) {
					list.remove(i);
					hasKey = true;
					break;
				}
			}
			if (project.focusedWindow != null) {
				project.focusedWindow.windowListener.keyReleased(_event);
			}
			if (!hasKey) {
				L.e("PressedKeys.removeKey()", "!hasKey(_integer)" + " _integer==" + keycode);
			}

		}

		return false;
	}

}
