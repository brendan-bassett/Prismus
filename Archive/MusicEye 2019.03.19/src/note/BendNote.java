package note;

import java.awt.Point;

import edit.Undoable;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;

public class BendNote extends ActionItem implements STYLE.NOTE, Undoable {


	private Note	note;
	private int		startBend;
	private Point	startPoint;
	private int		newBend	= 0;


	public BendNote(ScoreWindow _window, Note _note, Point _point) {
		super(_window);
		note = _note;
		startBend = note.bend;
		startPoint = _point;
	}


	@Override
	public void cancel() {}

	@Override
	public void mouseDragged(Point _point) {
		int dY = _point.y - startPoint.y;
		int dBend = (Math.round(dY * BEND_PER_PIXEL));
		newBend = startBend + dBend;

		if (newBend > BEND_LIMIT) {
			newBend = BEND_LIMIT;
		} else if (newBend < -BEND_LIMIT) {
			newBend = -BEND_LIMIT;
		}
		note.setBend(newBend);
	}

	@Override
	public void mouseReleased(Point _point) {
		int dY = _point.y - startPoint.y;
		int dBend = (Math.round(dY * BEND_PER_PIXEL));
		newBend = startBend + dBend;

		if (newBend > BEND_LIMIT) {
			newBend = BEND_LIMIT;
		} else if (newBend < -BEND_LIMIT) {
			newBend = -BEND_LIMIT;
		}
		note.setBend(newBend);

		window.removeActionItem();
		project.addUndoable(this);
	}

	@Override
	public void redo() {
		note.setBend(newBend);
	}

	@Override
	public void undo() {
		note.setBend(startBend);
	}

}
