package note;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import edit.Undoable;
import gui.CustomGraphics;
import gui.KeyManager;
import gui.Rect;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;

public class MultiSelect extends ActionItem implements STYLE.MULTI_SELECT {

	static private enum MODE {
		COPY,
		CUT,
		DRAG
	}


	final private Rect					selectRect			= new Rect();
	final private ArrayList<TempNote>	tempNoteList		= new ArrayList<TempNote>();

	private boolean						isFinishedSelecting	= false;
	private MODE						mode				= null;
	private Point						startPoint;


//	FIXME: finish creating MultiSelect & debug
	public MultiSelect(ScoreWindow _window, Point _point) {
		super(_window);

		startPoint = _point;
	}

	@Override
	public void cancel() {
		window.setCursor(CURSOR.DEFAULT);
	}

	@Override
	public void draw(CustomGraphics _g) {
		if (isFinishedSelecting) {
			for (TempNote tn : tempNoteList) {
				tn.draw(_g);
			}

		} else {
			_g.drawRect(selectRect, BORDER_COLOR);
		}
	}

	@Override
	public void keyPressed(KeyEvent _event) {
		if (isFinishedSelecting && mode == null) {
			if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
				MultiDelete multiDelete = new MultiDelete();
				multiDelete.redo();
				window.project.addUndoable(multiDelete);

				window.removeActionItem();

			} else if (KeyManager.has(KeyEvent.VK_CONTROL) && KeyManager.has(KeyEvent.VK_C)) {
				mode = MODE.COPY;

			} else if (KeyManager.has(KeyEvent.VK_CONTROL) && KeyManager.has(KeyEvent.VK_X)) {
				mode = MODE.CUT;
			}
		}
	}

	@Override
	public void mouseDragged(Point _point) {
		if (isFinishedSelecting) {
			if (mode == MODE.DRAG) {
				int dX = _point.x - startPoint.x;
				for (TempNote tn : tempNoteList) {
					tn.transpose(dX);
				}

			} else if (mode != null) { //COPY or CUT

			}

		} else {
			selectRect.set(_point.x, _point.y, startPoint.x, startPoint.y);
		}
	}

	@Override
	public void mousePressed(Point _point) {
		if (isFinishedSelecting) {
			if (mode == null && selectRect.contains(_point)) {
				mode = MODE.DRAG;

				startPoint = _point;

			} else if (mode != null) { //CUT or COPY

				startPoint = _point;
			}
		}
	}

	@Override
	public void mouseReleased(Point _point) {
		if (isFinishedSelecting) {
			if (mode == MODE.DRAG) {
				int dX = _point.x - startPoint.x;
				float dTime = window.timeFromPixels(dX);

				MultiDrag multiDrag = new MultiDrag(dTime);
				multiDrag.redo();
				window.project.addUndoable(multiDrag);
			}
			
			window.removeActionItem();

		} else { //!isFinishedSelecting
			selectRect.set(_point.x, _point.y, startPoint.x, startPoint.y);

			float leftTime = window.timeFromScreenX(selectRect.left);
			float topRelP = window.relPFromScreenY(selectRect.top);
			float rightTime = window.timeFromScreenX(selectRect.right);
			float bottomRelP = window.relPFromScreenY(selectRect.bottom);

			for (Note note : window.project.noteList) {
				if (note.idealRelP <= topRelP && note.idealRelP >= bottomRelP) {
					if ((note.startT >= leftTime && note.startT <= rightTime)	|| (note.endT >= leftTime && note.endT <= rightTime)
						|| (note.startT <= leftTime && note.endT >= rightTime)) {
						tempNoteList.add(new TempNote(note));
					}
				}
			}

			if (tempNoteList.size() > 0) {
				isFinishedSelecting = true;
			} else {
				cancel();
				window.removeActionItem();
			}
		}
	}


	private class MultiDelete implements Undoable {

		@Override
		public void undo() {
			for (TempNote tn : tempNoteList) {
				tn.note.initAdd();
			}
		}

		@Override
		public void redo() {
			for (TempNote tn : tempNoteList) {
				tn.note.invalidateRemove(null);
			}
		}

	}

	private class MultiDrag implements Undoable {

		final float dTime;


		MultiDrag(float _dTime) {
			dTime = _dTime;
		}

		@Override
		public void undo() {
			for (TempNote tn : tempNoteList) {
				tn.note.moveByTime(-dTime);
			}
		}

		@Override
		public void redo() {
			for (TempNote tn : tempNoteList) {
				tn.note.moveByTime(dTime);
			}
		}

	}


	private class TempNote {

		final Note	note;
		final Rect	originalRect;

		Rect		updatedRect	= new Rect();


		TempNote(Note _note) {
			note = _note;

			int l = window.screenXFromTime(_note.startT);
			int t = window.screenYFromRelP(_note.idealRelP + STYLE.NOTE.COMPLETE_REL_P_HEIGHT);
			int r = window.screenXFromTime(_note.endT);
			int b = window.screenYFromRelP(_note.idealRelP - STYLE.NOTE.COMPLETE_REL_P_HEIGHT);

			originalRect = new Rect(l, t, r, b);
		}


		void draw(CustomGraphics _g) {
			_g.drawRect(updatedRect, TEMP_NOTE_COLOR);
		}

		void transpose(int _dX) {
			updatedRect = originalRect.copy();
			updatedRect.move(_dX, 0);
		}

	}

}
