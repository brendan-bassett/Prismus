package note;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import edit.Undoable;
import gui.CustomGraphics;
import gui.CustomGraphics.Align;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;
import project.Graph;
import project.Part;
import project.Ratio;
import sound.SoundMenu;
import util.CustomException;
import util.K;

public class CreateNote extends ActionItem implements Undoable, STYLE.CREATE_NOTE {

	static private final int		NEW_NOTE_PLAY_DELAY		= 1500;							//in ms
	static private final float		NEW_NOTE_PLAY_VELOCITY	= 0.8f;


	private final SoundMenu			soundMenu;

	private Note					parentNote				= null;
	private Ratio					parentNoteParentRatio	= null;
	private Ratio					parentNoteRatio			= null;

	private final Part				part;
	private Graph					containingGraph			= null;

	private ArrayList<TempInterval>	intervalList			= new ArrayList<TempInterval>();
	private TempInterval			selectedInterval;

	private final float				leftTime;
	private final float				rightTime;
	private final boolean			beatSnapIsEnabled;

	private final ArrayList<Note>	visibleNoteList			= new ArrayList<Note>();
	private final ArrayList<Float>	beatTimeList			= new ArrayList<Float>();

	private float					time;
	private int						timeX;

	private boolean					isValid					= true;

	private Note					newNote					= null;


	public CreateNote(ScoreWindow _window, Graph _graph, Note _parentNote, Point _point) throws CustomException {
		this(_window, _graph, _parentNote.idealRelP, _point);

		parentNote = _parentNote;
		parentNoteParentRatio = parentNote.parentRatio;
		parentNoteRatio = parentNote.ratio;
	}

	public CreateNote(ScoreWindow _window, Graph _graph, float _parentRelP, Point _point) throws CustomException {
		super(_window);

		soundMenu = _window.project.mainMenu.soundMenu;

		part = project.partManager.getSelectedPart();
		containingGraph = _graph;

		final float tRelP = window.relPFromScreenY(window.drawRect.top);
		float cRelP = _parentRelP;
		Ratio ratio = new Ratio(1, 1);

		selectedInterval = new TempInterval(window, cRelP, time, ratio);
		intervalList.add(selectedInterval);

		oTonal: for (int i = 0; cRelP < tRelP; i++) {
			for (Ratio dr : project.defaultRatios) {
				ratio = new Ratio(i, dr.numerator, dr.denominator);
				cRelP = (_parentRelP + ratio.relP);

				if (ratio.relP == 0) {
					continue;
				}
				if (cRelP >= tRelP) {
					break oTonal;
				} else {
					intervalList.add(new TempInterval(window, cRelP, time, ratio));
				}
			}
		}

		final float bRelP = window.relPFromScreenY(window.drawRect.bottom);
		cRelP = _parentRelP;

		uTonal: for (int i = -1; cRelP > bRelP; i--) {
			for (int j = (project.defaultRatios.size() - 1); j >= 0; j--) {
				Ratio dr = project.defaultRatios.get(j);
				ratio = new Ratio(i, dr.numerator, dr.denominator);
				cRelP = (_parentRelP + ratio.relP);

				if (cRelP <= bRelP) {
					break uTonal;
				} else {
					intervalList.add(new TempInterval(window, cRelP, time, ratio));
				}
			}
		}

		leftTime = _window.timeFromScreenX(_window.drawRect.left);
		rightTime = _window.timeFromScreenX(_window.drawRect.right);
		beatSnapIsEnabled = project.mainMenu.editMenu.isBeatSnapEnabled();

		for (Note note : project.noteList) {
			if (note.part != part) {
				continue;
			}

			if ((note.endT > leftTime && note.endT < rightTime) || (note.startT > leftTime && note.startT < rightTime)) {
				visibleNoteList.add(note);
			} else if (note.startT < leftTime && note.endT > rightTime) {
				throw new CustomException("CreateNote()" + "only one note is visible and it covers the entire screen");
			}
		}

		if (beatSnapIsEnabled) {
			ArrayList<Graph> graphList = new ArrayList<Graph>();
			for (Graph graph : project.graphList) {
				if (graph.startTime < leftTime && graph.endTime > rightTime) {
					graphList.add(graph);
					break;
				} else if ((graph.endTime > leftTime && graph.endTime < rightTime)
							|| (graph.startTime > leftTime && graph.startTime < rightTime)) {
					graphList.add(graph);
				}
			}

			ArrayList<Float> visibleBeatTimeList = new ArrayList<Float>();
			for (Graph graph : graphList) {
				float timePerBeatSubd = (graph.getTimePerBeat(1) * STYLE.GRAPH.BEAT_SUBDIVISION);

				for (float beatTime = graph.startTime; beatTime < graph.endTime; beatTime = (beatTime + timePerBeatSubd)) {
					if (beatTime > leftTime && beatTime < rightTime) {
						visibleBeatTimeList.add(beatTime);
					}
				}
			}

			beats: for (Float beatTime : visibleBeatTimeList) {
				for (Note note : visibleNoteList) {
					if (beatTime >= note.startT && beatTime < note.endT) {
						continue beats;
					}
				}

				beatTimeList.add(beatTime);
			}

			if (beatTimeList.size() == 0) {
				throw new CustomException("CreateNote()", "no available beat times to snap to");
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
		for (TempInterval ti : intervalList) {
			ti.draw(_g);
		}

		if (time == 0) {
			_g.line(timeX, window.drawRect.top, timeX, window.drawRect.bottom, INVALID_COLOR);
		} else {
			_g.line(timeX, window.drawRect.top, timeX, window.drawRect.bottom, VALID_COLOR);
		}
	}

	@Override
	public void mouseDragged(Point _point) {
		snap(window.timeFromScreenX(_point.x));

		if (time > containingGraph.endTime || time < containingGraph.startTime) {
			for (Graph g : project.graphList) {
				if (time >= g.startTime && time <= g.endTime) {
					containingGraph = g;
				}
			}
		}

		int dY = window.pixelsFromRelP(1);
		TempInterval newSelectedInterval = selectedInterval;
		for (int i = 0; i < intervalList.size(); i++) {
			TempInterval interval = intervalList.get(i);
			interval.setChildT(time);

			int iDY = Math.abs(_point.y - window.screenYFromRelP(interval.relP));
			if (iDY < dY) {
				newSelectedInterval = interval;
				dY = iDY;
			}
		}

		if (selectedInterval != newSelectedInterval) {
			selectedInterval.setIsSelected(false);
			selectedInterval = newSelectedInterval;
			selectedInterval.setIsSelected(true);
		}

		resize();
	}

	@Override
	public void mouseReleased(Point _point) {
		snap(window.timeFromScreenX(_point.x));

		if (time > containingGraph.endTime || time < containingGraph.startTime) {
			if (time > containingGraph.endTime || time < containingGraph.startTime) {
				for (Graph g : project.graphList) {
					if (time >= g.startTime && time <= g.endTime) {
						containingGraph = g;
					}
				}
			}
		}

		if (isValid) {
			selectedInterval.setChildT(time);

			Ratio newParentRatio = null;
			Ratio newRatio = null;

			if (parentNote != null) {
				if (selectedInterval.ratio.isUnison) {
					newParentRatio = parentNoteParentRatio;
					newRatio = parentNoteRatio;
				} else {
					newParentRatio = Ratio.multiply(parentNoteParentRatio, parentNoteRatio);
					newRatio = selectedInterval.ratio;
				}

			} else {
				newParentRatio = new Ratio();
				newRatio = selectedInterval.ratio;
			}

			newNote = new Note(	project,
								containingGraph,
								project.getNewNoteID(),
								newParentRatio,
								part,
								newRatio,
								selectedInterval.relP,
								time);
			newNote.initAdd();
			window.giveFocus(newNote);

			float hz = K.hzFromRelP(project, newNote.relP);
			soundMenu.playNote(part, hz, NEW_NOTE_PLAY_DELAY, NEW_NOTE_PLAY_VELOCITY);

			project.addUndoable(this);
		}

		window.removeActionItem();
	}

	@Override
	public void redo() {
		newNote.initAdd();
	}

	@Override
	public void resize() {
		for (TempInterval ti : intervalList) {
			ti.resize();
		}
		timeX = window.screenXFromTime(time);
	}

	@Override
	public void undo() {
		newNote.invalidateRemove(parentNote);
//		newInterval removed through newNote.invalidateRemove()
	}

	private void snap(float _time) {
		if (_time < 0) {
			_time = 0;
		}

		for (Note note : visibleNoteList) {
			if (_time >= note.startT && _time < note.endT) {
				time = note.endT;

				if (time <= leftTime || time >= rightTime) {
					isValid = false;
					return;

				} else {
					isValid = true;
					return;
				}
			}
		}

		if (beatSnapIsEnabled) {
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
		}
	}


	private class TempInterval implements STYLE.NOTE {

		private float				relP;
		private float				time;
		private Ratio				ratio;

		private final ScoreWindow	window;

		private boolean				isSelected	= false;
		private int					x;
		private int					y;
		private String				text;


		private TempInterval(ScoreWindow _window, float _relP, float _time, Ratio _ratio) {
			relP = _relP;
			time = _time;
			ratio = _ratio;

			window = _window;
			resize();
		}


		public void draw(CustomGraphics _g) {
			Color color;
			int lineLength;
			if (isSelected) {
				color = SELECTED_COLOR;
				lineLength = SELECTED_LINE_LENGTH;
			} else {
				color = UNSELECTED_COLOR;
				lineLength = UNSELECTED_LINE_LENGTH;
			}

			if (ratio.isUnison) {
				_g.fillCircle(x, y, UNISON_RADIUS, color);
				_g.text(text, (x - UNISON_RADIUS - INTERVAL_TEXT_MARGIN), (y - INTERVAL_TEXT_MARGIN), Align.RIGHT_TOP, FONT, TEXT_COLOR);

			} else {
				_g.line((x - lineLength), y, (x + lineLength), y, color);
				_g.text(text, (x - lineLength - INTERVAL_TEXT_MARGIN), y, Align.RIGHT_CENTER, FONT, TEXT_COLOR);
			}
		}

		public void setChildT(float _childT) {
			time = _childT;
		}

		public void resize() {
			x = window.screenXFromTime(time);
			y = window.screenYFromRelP(relP);
			text = ratio.toString();
		}

		public void setIsSelected(boolean _isSelected) {
			isSelected = _isSelected;
		}

	}

}
