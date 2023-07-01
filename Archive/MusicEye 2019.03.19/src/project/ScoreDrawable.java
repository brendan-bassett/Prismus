package project;

import gui.CustomGraphics;
import gui.Rect;
import gui.ScoreWindow;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import edit.Undoable;
import util.L;

abstract public class ScoreDrawable extends Rect {

	private class ScoreItemDelete implements Undoable {

		private final ScoreItem scoreItem;


		private ScoreItemDelete(ScoreItem _scoreItem) {
			scoreItem = _scoreItem;
		}


		@Override
		public void undo() {
			scoreItem.initAdd();
		}

		@Override
		public void redo() {
			scoreItem.invalidateRemove(null);
		}

	}


	public final ScoreItem	scoreItem;
	public final ScoreWindow		window;

	protected final Project	project;

	protected boolean		hasFocus	= false;
	protected final boolean	canDelete;


	public ScoreDrawable(ScoreWindow _window, ScoreItem _scoreItem, boolean _canDelete) {
		window = _window;
		project = window.project;
		scoreItem = _scoreItem;
		canDelete = _canDelete;
	}


	abstract protected void draw(CustomGraphics _g);

	abstract protected void mouseDragged(Point _point);

	abstract protected void mouseEntered(Point _point);

	abstract protected void mouseExited(Point _point);

	abstract protected void mouseMoved(Point _point);

	abstract protected ActionItem mousePressed(Point _point);

	abstract protected void mouseReleased(Point _point);

	abstract protected void resize();


	protected void createPopupMenu(MouseEvent _event) {
//		DO NOTHING
	}

	public void keyPressed(KeyEvent _event) {
		if (canDelete) {
			if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
				scoreItem.invalidateRemove(null);

				ScoreItemDelete scoreItemDelete = new ScoreItemDelete(scoreItem);
				project.addUndoable(scoreItemDelete);
			}
			project.repaint();
		}
	}

	public void keyReleased(KeyEvent _event) {}

	public void setHasFocus(boolean _hasFocus) {
		hasFocus = _hasFocus;
	}

}
