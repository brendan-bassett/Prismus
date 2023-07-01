package note;

import java.awt.Point;
import java.util.ArrayList;

import edit.Undoable;
import gui.STYLE;
import gui.ScoreWindow;
import note.Note.EDIT_ZONE;
import project.ActionItem;
import project.Graph;
import util.CustomException;
import util.L;


class MoveNote extends ActionItem implements Undoable, STYLE.GLOBAL {

	private final Note			note;
	private final float			undoT;

	final float					leftTime;
	final float					rightTime;
	final boolean				beatSnapIsEnabled;
	final ArrayList<Float>		beatTimeList	= new ArrayList<Float>();
	private final AbstractMove	abstractMove;

	private float				time;
	protected boolean			isValid			= true;


	/**
	 * 
	 * @param _window
	 * @param _note
	 * @param _point
	 * @param _zone
	 * @throws CustomException if zone!=ZONE.START || zone!=ZONE.END ; or if beatSnapper or partSnapper could not be created
	 */
	MoveNote(ScoreWindow _window, Note _note, Point _point, EDIT_ZONE _zone) throws CustomException {
		super(_window);

		note = _note;

		leftTime = window.timeFromScreenX(window.drawRect.left);
		rightTime = window.timeFromScreenX(window.drawRect.right);
		beatSnapIsEnabled = project.mainMenu.editMenu.isBeatSnapEnabled();

		if (_zone == EDIT_ZONE.START) {
			undoT = note.startT;
			abstractMove = new MoveStart();

		} else if (_zone == EDIT_ZONE.END) {
			undoT = note.endT;
			abstractMove = new MoveEnd();

		} else {
			throw new CustomException("MoveNote()", "zone!=ZONE.START || zone!=ZONE.END");
		}

		abstractMove.snap(window.timeFromScreenX(_point.x));
	}


	@Override
	public void cancel() {
		window.removeActionItem();
		window.setCursor(CURSOR.DEFAULT);
	}


	@Override
	public void undo() {
		abstractMove.undo();
	}


	@Override
	public void redo() {
		abstractMove.redo();
	}


	@Override
	public void mouseDragged(Point _point) {
		abstractMove.snap(window.timeFromScreenX(_point.x));
	}


	@Override
	public void mouseReleased(Point _point) {
		abstractMove.snap(window.timeFromScreenX(_point.x));

		project.addUndoable(this);
		window.removeActionItem();
		window.setCursor(CURSOR.DEFAULT);
	}


	private abstract class AbstractMove {

		Note adjacentNote;


		AbstractMove() throws CustomException {}


		abstract void redo();

		abstract void snap(float _time);

		abstract void undo();

	}


	private class MoveEnd extends AbstractMove {

		MoveEnd() throws CustomException {

			Note tempAdjacentNote = null;
			float dTempTime = Float.MAX_VALUE;
			for (Note lNote : project.noteList) {
				if (lNote.part != note.part) {
					continue;
				}

				float dTime = lNote.startT - note.startT;
				if (dTime > 0 && dTime < dTempTime) {
					if (lNote.endT > rightTime && lNote.startT < leftTime) {
						throw new CustomException("MoveEnd()", "one note visible in part and it covers entire screen");
					}

					tempAdjacentNote = lNote;
					dTempTime = dTime;
				}
			}
			adjacentNote = tempAdjacentNote;

			if (beatSnapIsEnabled) {
				L.l("MoveNote.MoveEnd()", "beatSnapIsEnabled==true");
				ArrayList<Graph> graphList = new ArrayList<Graph>();
				for (Graph graph : project.graphList) {
					if (graph.startTime < note.startT && graph.endTime > rightTime) {
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
							if (adjacentNote == null || (adjacentNote != null && beatTime <= adjacentNote.startT)) {
								beatTimeList.add(beatTime);
							}
						}
					}
				}

				if (beatTimeList.size() == 0) {
					throw new CustomException("MoveEnd()", "no available beat times to snap to");
				}
			}
		}

		@Override
		void redo() {
			note.setEndT(time);
		}

		@Override
		void snap(float _time) {
			if (_time <= note.startT) {
				isValid = false;
				time = _time;

			} else if (_time < 0) {
				isValid = false;
				time = 0;

			} else if (adjacentNote != null && _time > adjacentNote.startT) {
				isValid = true;
				time = adjacentNote.startT;

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
				time = returnTime;

			} else {
				isValid = true;
				time = _time;
			}

			if (isValid) {
				note.setEndT(time);
			}
		}

		@Override
		void undo() {
			note.setEndT(undoT);
		}

	}


	private class MoveStart extends AbstractMove {


		MoveStart() throws CustomException {

			Note tempAdjacentNote = null;
			float tempDTime = Float.MAX_VALUE;
			for (Note lNote : project.noteList) {
				if (lNote.part != note.part) {
					continue;
				}

				float dTime = note.endT - lNote.endT;
				if (dTime > 0 && dTime < tempDTime) {
					if (lNote.endT > rightTime && lNote.startT < leftTime) {
						throw new CustomException("MoveEnd()", "one note visible in part and it covers entire screen");
					}

					tempAdjacentNote = lNote;
					tempDTime = dTime;
				}
			}
			adjacentNote = tempAdjacentNote;

			if (beatSnapIsEnabled) {
				ArrayList<Graph> graphList = new ArrayList<Graph>();
				for (Graph graph : project.graphList) {
					if (graph.startTime < leftTime && graph.endTime > note.endT) {
						graphList.add(graph);
						break;
					} else if ((graph.endTime > leftTime && graph.endTime < note.endT)
								|| (graph.startTime > leftTime && graph.startTime < note.endT)) {
						graphList.add(graph);
					}
				}

				for (Graph graph : graphList) {
					float timePerBeatSubd = (graph.getTimePerBeat(1) * STYLE.GRAPH.BEAT_SUBDIVISION);

					for (float beatTime = graph.startTime; beatTime < graph.endTime; beatTime = (beatTime + timePerBeatSubd)) {
						if (beatTime > leftTime && beatTime < note.endT) {
							if (adjacentNote == null) {
								beatTimeList.add(beatTime);
							} else if (adjacentNote != null && beatTime >= adjacentNote.endT) {
								beatTimeList.add(beatTime);
							}
						}
					}
				}

				if (beatTimeList.size() == 0) {
					throw new CustomException("MoveEnd()", "no available beat times to snap to");
				}
			}
		}

		@Override
		public void redo() {
			note.setStartT(time);
		}

		@Override
		void snap(float _time) {
			if (_time >= note.endT) {
				isValid = false;
				time = _time;

			} else if (_time < 0) {
				isValid = false;
				time = 0;

			} else if (adjacentNote != null && _time < adjacentNote.endT) {
				isValid = true;
				time = adjacentNote.endT;

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
				time = returnTime;

			} else {
				isValid = true;
				time = _time;
			}

			if (isValid) {
				note.setStartT(time);
			}
		}

		@Override
		public void undo() {
			note.setStartT(undoT);
		}

	}


}
