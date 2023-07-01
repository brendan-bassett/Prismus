
package note;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import edit.Undoable;
import gui.CustomGraphics;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;
import project.Graph;
import util.CustomException;

public class CompleteNote extends ActionItem implements Undoable, STYLE.NOTE {

	private final Note				note;

	private Polygon					triangle;
	private float					endT;
	private int						endX;
	private boolean					isValid			= true;

	private final float				leftTime;
	private final float				rightTime;
	private final boolean			beatSnapIsEnabled;

	private final ArrayList<Float>	beatTimeList	= new ArrayList<Float>();
	private Note					adjacentNote	= null;


	public CompleteNote(ScoreWindow _window, Note _note, Point _point) throws CustomException {
		super(_window);
		note = _note;

		leftTime = _window.timeFromScreenX(_window.drawRect.left);
		rightTime = _window.timeFromScreenX(_window.drawRect.right);
		beatSnapIsEnabled = project.mainMenu.editMenu.isBeatSnapEnabled();

		Note tempAdjacentNote = null;
		float dTempTime = Float.MAX_VALUE;
		for (Note lNote : project.noteList) {
			if (lNote.part != note.part) {
				continue;
			}

			float dTime = lNote.startT - note.startT;
			if (dTime > 0 && dTime < dTempTime) {
				if (lNote.endT > rightTime && lNote.startT < leftTime) {
					throw new CustomException("CompleteNote()", "one note visible in part and it covers entire screen");
				}

				tempAdjacentNote = lNote;
				dTempTime = dTime;
			}
		}
		adjacentNote = tempAdjacentNote;

		if (beatSnapIsEnabled) {
			ArrayList<Graph> graphList = new ArrayList<Graph>();
			for (Graph graph : project.graphList) {
				if (graph.startTime <= note.startT && graph.endTime > rightTime) {
					graphList.add(graph);
					break;
				} else if ((graph.endTime > note.startT && graph.endTime < rightTime)
							|| (graph.startTime > note.startT && graph.startTime < rightTime)) {
					graphList.add(graph);
				}
			}

			for (Graph graph : graphList) {
				float timePerBeatSubd = (graph.getTimePerBeat(1) * STYLE.GRAPH.BEAT_SUBDIVISION);

				for (float beatTime = graph.startTime; beatTime < graph.endTime; beatTime = (beatTime + timePerBeatSubd)) {
					if (beatTime > note.startT && beatTime < rightTime) {
						if (adjacentNote == null) {
							beatTimeList.add(beatTime);
						} else if (adjacentNote != null && beatTime <= adjacentNote.startT) {
							beatTimeList.add(beatTime);
						}
					}
				}
			}

			if (beatTimeList.size() == 0) {
				throw new CustomException("CompleteNote()", "no available beat times to snap to");
			}
		}

		snap(window.timeFromScreenX(_point.x));

		resize();
	}


	@Override
	public void cancel() {
		window.removeActionItem();
		window.setCursor(CURSOR.DEFAULT);
	}

	@Override
	public void draw(CustomGraphics _g) {
		Color borderColor;
		if (isValid) {
			borderColor = COMPLETE_VALID_COLOR;
		} else {
			borderColor = COMPLETE_INVALID_COLOR;
		}

		_g.line(endX, window.drawRect.top, endX, window.drawRect.bottom, borderColor);
		_g.fillPolygon(triangle, note.part.color);
		_g.drawPolygon(triangle, FOCUSED_BORDER_COLOR);
	}

	@Override
	public void mouseDragged(Point _point) {
		snap(window.timeFromScreenX(_point.x));

		resize();
	}

	@Override
	public void mouseReleased(Point _point) {
		snap(window.timeFromScreenX(_point.x));

		if (isValid) {
			note.complete(endT);
			project.addUndoable(this);
		}

		window.removeActionItem();
	}

	@Override
	public void resize() {
		int top = window.screenYFromRelP(note.idealRelP + COMPLETE_REL_P_HEIGHT);
		int bottom = window.screenYFromRelP(note.relP - COMPLETE_REL_P_HEIGHT);
		int centerY = (top + bottom) / 2;
		int right = window.screenXFromTime(endT);
		int left = (right - ((top - bottom) / 2));

		int[] tY = { top, bottom, centerY };
		int[] tX = { right, right, left };
		triangle = new Polygon(tX, tY, 3);
	}


	@Override
	public void undo() {
		note.undoComplete();
	}


	@Override
	public void redo() {
		note.complete(endT);
	}


	private void snap(float _time) {
		if (_time <= note.startT) {
			isValid = false;
			endT = note.startT;

		} else if (_time < 0) {
			isValid = false;
			endT = 0;

		} else if (adjacentNote != null && _time > adjacentNote.startT) {
			isValid = true;
			endT = adjacentNote.startT;

		} else if (beatSnapIsEnabled) {
			float returnTime = 0;
			float dTime = _time;
			for (Float beatTime : beatTimeList) {
				float dBeatTime = Math.abs(beatTime - _time);
				if (dBeatTime < dTime) {
					returnTime = beatTime;
					dTime = dBeatTime;
				}
			}

			isValid = true;
			endT = returnTime;

		} else {
			isValid = true;
			endT = _time;
		}

		if (isValid) {
			note.setEndT(endT);
		}
	}

}
