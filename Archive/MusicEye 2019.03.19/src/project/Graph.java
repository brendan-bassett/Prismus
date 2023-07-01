package project;

import java.awt.Point;
import java.util.ArrayList;

import file.FILE;
import file.FileElement;
import gui.CustomGraphics;
import gui.CustomGraphics.Align;
import gui.Rect;
import gui.STYLE;
import gui.ScoreWindow;
import gui.VisibleRange;
import util.CustomException;
import util.K;

/**
 * Created by Brendan on 5/10/2015.
 */
public class Graph extends ScoreItem implements FILE.GRAPH_F {

	static public Graph read(Project _project, FileElement _e) throws CustomException {
		int beatsPerMeasure = _e.get(BEATS_PER_MEASURE);
		float endTime = _e.get(END_TIME);
		int id = _e.get(ID);
		int leadBeats = _e.get(LEAD_BEATS);
		int measures = _e.get(MEASURES);
		int measureNumStart = _e.get(MEASURE_NUM_START);
		Ratio ratio = Ratio.read(_e.getChild(RATIO_TAG));
		float startTime = _e.get(START_TIME);
		float tempo = _e.get(TEMPO);

		return new Graph(_project, beatsPerMeasure, endTime, id, leadBeats, measures, measureNumStart, ratio, startTime, tempo);
	}


	private int			beatsPerMeasure;
	public float		endTime;
	public final int	id;
	private int			leadBeats;
	private int			measures;
	private int			measureNumStart;
	private Ratio		ratio;
	public float		startTime;
	private float		tempo;			// beats per minute


// TODO: Implement adding, removing and changing Graphs

	public Graph(	Project _project,
					int _beatsPerMeasure,
					int _id,
					int _leadBeats,
					int _measures,
					int _measureNumStart,
					Ratio _ratio,
					float _startTime,
					float _tempo) {

		super(Type.GRAPH, _project);

		beatsPerMeasure = _beatsPerMeasure;
		id = _id;
		leadBeats = _leadBeats;
		measures = _measures;
		measureNumStart = _measureNumStart;
		ratio = _ratio;
		startTime = _startTime;
		tempo = _tempo;

		endTime = (startTime + ((beatsPerMeasure * measures + leadBeats) * tempo / K.SECONDS_PER_MINUTE));

		for (int i = (int) project.bottomRelP; i <= (int) project.topRelP; i++) {
			TonicButton tonicButton = new TonicButton(project, this, i, i);
			tonicButton.initAdd();
		}
	}

	public Graph(	Project _project,
					int _beatsPerMeasure,
					float _endTime,
					int _id,
					int _leadBeats,
					int _measures,
					int _measureNumStart,
					Ratio _ratio,
					float _startTime,
					float _tempo) {

		super(Type.GRAPH, _project);

		beatsPerMeasure = _beatsPerMeasure;
		endTime = _endTime;
		id = _id;
		leadBeats = _leadBeats;
		measures = _measures;
		measureNumStart = _measureNumStart;
		ratio = _ratio;
		startTime = _startTime;
		tempo = _tempo;
	}


	public int beatsContained() {
		return (beatsPerMeasure * measures) + leadBeats;
	}

	@Override
	protected Bounds createBounds(ScoreWindow _window) {

		class GraphBounds extends Bounds {

			private GraphBounds(Graph _graph, ScoreWindow _window) {
				super(_graph, _window);
			}

			@Override
			protected boolean checkVisibility() {
				VisibleRange windowLimit = window.visibleRange;

				if (startTime < windowLimit.leftT && endTime > windowLimit.rightT) {
					return true;
				} else if (startTime > windowLimit.leftT && startTime < windowLimit.rightT) {
					return true;
				} else if (endTime > windowLimit.leftT && endTime < windowLimit.rightT) {
					return true;
				}
				return false;
			}

		}

		return new GraphBounds(this, _window);
	}

	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		return new GraphDrawable(_window, this);
	}

	public float getTimePerBeat(float _beat) {
		return (1 / tempo) * K.SECONDS_PER_MINUTE;
	}

	public void write(FileElement _e) throws CustomException {
		_e.add(BEATS_PER_MEASURE, beatsPerMeasure);
		_e.add(END_TIME, endTime);
		_e.add(ID, id);
		_e.add(LEAD_BEATS, leadBeats);
		_e.add(MEASURES, measures);
		_e.add(MEASURE_NUM_START, measureNumStart);
		ratio.write(_e.createChild(RATIO_TAG));
		_e.add(START_TIME, startTime);
		_e.add(TEMPO, tempo);
	}


	private class GraphDrawable extends ScoreDrawable implements STYLE.GRAPH {

		private int[]						beatXList	= new int[0];
		private final ArrayList<Integer>	tonicYList	= new ArrayList<Integer>();
		private int							primeTonicY;


		private GraphDrawable(ScoreWindow _window, Graph _graph) {
			super(_window, _graph, false);
			resize();
		}


		@Override
		public boolean contains(Point _point) {
			return false;
		}

		@Override
		protected void draw(CustomGraphics _g) {
			int beatOfMeasure = beatsPerMeasure - leadBeats;

			// BEAT LINES
			for (int i = 0; i < beatXList.length; i++) {
				int x = beatXList[i];

				if (x > window.drawRect.left || x > window.drawRect.right) {
					if (i == 0) {
						_g.line((x - START_LINES_D_X),
								window.drawRect.top,
								(x - START_LINES_D_X),
								window.drawRect.bottom,
								START_LINE_COLOR);
						_g.line(x, window.drawRect.top, x, window.drawRect.bottom, START_LINE_COLOR, START_LINE_STROKE);

					} else if (beatOfMeasure == 0) {
						_g.line(x, window.drawRect.top, x, window.drawRect.bottom, ZERO_BEAT_COLOR, ZERO_BEAT_STROKE);

					} else {
						_g.line(x, window.drawRect.top, x, window.drawRect.bottom, BEAT_COLOR);
					}
				}

				beatOfMeasure++;
				if (beatOfMeasure == beatsPerMeasure) {
					beatOfMeasure = 0;
				}
			}

			// TONIC LINES
			int lineLeft;
			if (window.drawRect.left < left) {
				lineLeft = left;
			} else {
				lineLeft = window.drawRect.left;
			}

			int lineRight;
			if (window.drawRect.right > right) {
				lineRight = right;
			} else {
				lineRight = window.drawRect.right;
			}

			_g.line(lineLeft, primeTonicY, lineRight, primeTonicY, PRIME_TONIC_COLOR, PRIME_TONIC_STROKE);

			for (Integer y : tonicYList) {
				if (y > window.drawRect.bottom || y < window.drawRect.top) {
					_g.line(lineLeft, y, lineRight, y, TONIC_COLOR);
				}
			}

			// MEASURE NUMBERS
			beatOfMeasure = beatsPerMeasure - leadBeats;
			int measureNumber = measureNumStart;

			for (int i = 0; i < beatXList.length; i++) {
				int x = beatXList[i];

				if (x > window.drawRect.left || x > window.drawRect.right) {
					if (beatOfMeasure == 0) {

						String string = Integer.toString(measureNumber);
						int y = window.drawRect.top - MEASURE_NUM_Y_MARGIN;
						Rect rect = MEASURE_FONT.getEnclosingRect(window, string, MEASURE_NUM_INSETS, x, y, Align.CENTER_TOP, false);

						_g.fillRect(rect, MEASURE_NUM_BG_COLOR);
						_g.drawRect(rect, ZERO_BEAT_COLOR);
						_g.text(string, x, y, Align.CENTER_TOP, MEASURE_FONT, ZERO_BEAT_COLOR);

					}
				}

				beatOfMeasure++;
				if (beatOfMeasure == beatsPerMeasure) {
					beatOfMeasure = 0;
					measureNumber++;
				}
			}

		}

		@Override
		protected void mouseDragged(Point _point) {
		}

		@Override
		protected void mouseEntered(Point _point) {
		}

		@Override
		protected void mouseExited(Point _point) {
		}

		@Override
		protected void mouseMoved(Point _point) {
		}

		@Override
		protected ActionItem mousePressed(Point _point) {
			return null;
		}

		@Override
		protected void mouseReleased(Point _point) {
		}

		@Override
		public void move(int _dX, int _dY) {
			super.move(_dX, _dY);

			for (int i = 0; i < beatXList.length; i++) {
				beatXList[i] = beatXList[i] + _dX;
			}

			primeTonicY = primeTonicY + _dY;
			for (int i = 0; i < tonicYList.size(); i++) {
				Integer newInt = tonicYList.get(i) + _dY;
				tonicYList.set(i, newInt);
			}
		}

		@Override
		protected void resize() {
			int l = window.screenXFromTime(startTime);
			int r = window.screenXFromTime(endTime);
			int t = window.drawRect.top;
			int b = window.drawRect.bottom;
			set(l, t, r, b);


			float timeCounter = startTime;
			beatXList = new int[beatsContained()];

			for (int i = 0; i < beatsContained(); i++) {
				beatXList[i] = window.screenXFromTime(timeCounter);
				timeCounter = timeCounter + (1 / tempo * K.SECONDS_PER_MINUTE);
			}

			tonicYList.clear();
			float relP = ratio.relP;
			primeTonicY = window.screenYFromRelP(relP);

			for (int i = -1; relP > project.bottomRelP; i--) {
				relP = ratio.relP + i;
				tonicYList.add(window.screenYFromRelP(relP));
			}

			for (int i = 1; relP < project.topRelP; i++) {
				relP = ratio.relP + i;
				tonicYList.add(window.screenYFromRelP(relP));
			}
		}

	}

}