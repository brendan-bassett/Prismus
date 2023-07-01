package note;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import file.FILE;
import file.FileElement;
import gui.CustomGraphics;
import gui.KeyManager;
import gui.Rect;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;
import project.Bounds;
import project.Graph;
import project.Part;
import project.Project;
import project.Ratio;
import project.ScoreDrawable;
import project.ScoreItem;
import util.CustomException;
import util.L;
import util.K;

/**
 * Created by Brendan on 3/31/2015.
 */
public class Note extends ScoreItem implements FILE.NOTE_F {

	static public enum EDIT_ZONE {
		CENTER,
		END,
		NONE,
		START
	}


	static public Note read(Project _project, FileElement _e) throws CustomException {
		int bend = _e.get(BEND);
		Float endT = _e.get(END_T);
		Float hz = _e.get(HZ);
		int id = _e.get(ID);
		Float idealRelP = _e.get(IDEAL_REL_P);
		boolean isComplete = _e.get(IS_COMPLETE);
		boolean isSimplified = _e.get(IS_SIMPLIFIED);
		Ratio parentRatio = Ratio.read(_e.getChild(PARENT_RATIO_TAG));
		Ratio ratio = Ratio.read(_e.getChild(RATIO_TAG));
		Float relP = _e.get(REL_P);
		boolean showOvertones = _e.get(SHOW_OVERTONES);
		Float startT = _e.get(START_T);

		int graphID = _e.get(GRAPH_ID);
		if (graphID == ScoreItem.NULL_ID) {
			throw new CustomException("could not read note. graphID == NULL_ID");
		}
		Graph graph = null;
		for (Graph g : _project.graphList) {
			if (g.id == graphID) {
				graph = g;
			}
		}

		String partName = _e.get(PART_NAME);
		Part part = null;
		for (Part p : _project.partList) {
			if (p.name.matches(partName)) {
				part = p;
			}
		}

		if (graph != null && part != null) {
			return new Note(_project,
							bend,
							endT,
							graph,
							hz,
							id,
							idealRelP,
							isComplete,
							isSimplified,
							parentRatio,
							part,
							ratio,
							relP,
							showOvertones,
							startT);	
		} else {
			throw new CustomException("Note.read", "graph or part == null");
		}
	}


	int					bend		= 0;
	public float		endT;
	private Graph		graph;
	public float		hz;
	public final int	id;
	public float		idealRelP;
	public boolean		isComplete;
	boolean				isSimplified;
	public Ratio		parentRatio;
	public Part			part;
	public Ratio		ratio;
	public float		relP;
	public float		startT;


	Overtones			overtones	= null;


	public Note(Project _project, Graph _graph, int _id, Ratio _parentRatio, Part _part, Ratio _ratio, float _relP, float _startT) {
		super(Type.NOTE, _project);

//		TODO: allow for multiple notes (in separate parts) to share same note space
//		TODO: create cut & copy & paste functions for multiple Notes

		graph = _graph;
		id = _id;
		parentRatio = _parentRatio;
		part = _part;
		ratio = _ratio;
		relP = _relP;
		startT = _startT;

		endT = startT;
		hz = K.hzFromRelP(project, relP);
		idealRelP = _relP;
		isComplete = false;

		if (parentRatio.isUnison) {
			isSimplified = true;
		} else {
			isSimplified = false;
		}
	}

	public Note(Project _project,
				int _bend,
				float _endT,
				Graph _graph,
				float _hz,
				int _id,
				float _idealRelP,
				boolean _isComplete,
				boolean _isSimplified,
				Ratio _parentRatio,
				Part _part,
				Ratio _ratio,
				float _relP,
				boolean _showOvertones,
				float _startT) {

		super(Type.NOTE, _project);

		bend = _bend;
		endT = _endT;
		graph = _graph;
		hz = _hz;
		id = _id;
		idealRelP = _idealRelP;
		isComplete = _isComplete;
		isSimplified = _isSimplified;
		parentRatio = _parentRatio;
		part = _part;
		ratio = _ratio;
		relP = _relP;
		startT = _startT;

		if (_showOvertones) {
			overtones = new Overtones(project, this);
			_project.add(overtones);
		}
	}


	@Override
	protected Bounds createBounds(ScoreWindow _window) {

		class NoteBounds extends Bounds {

			NoteBounds(Note _note, ScoreWindow _window) {
				super(_note, _window);
			}


			@Override
			protected boolean checkVisibility() {
				return window.visibleRange.intersects(startT, relP, endT);
			}

		}

		return new NoteBounds(this, _window);
	}

	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		if (isComplete) {
			return new CompleteNoteDrawable(_window, this);
		} else {
			return new IncompleteNoteDrawable(_window, this);
		}
	}

	void complete(float _endT) {
		if (isComplete) {
			L.e("Note.complete()", "note is already complete!");
			return;
		}

		endT = _endT;
		isComplete = true;

		for (ScoreWindow window : project.windowList) {
			window.resizeBounds(this, true);
		}
	}

	/**
	 * Determines whether or not the given time intersects is BETWEEN this note's start and end
	 * 
	 * @param _time
	 * @return
	 */
	public boolean contains(float _time) {
		if (_time > startT && _time < endT) {
			return true;
		} else {
			return false;
		}
	}

	public float getCenterT() {
		return ((startT + endT) / 2);
	}

	/**
	 * Determines whether or not the given note intersects this note IN TIME
	 * 
	 * @param _note
	 * @return
	 */
	public boolean intersects(Note _note) {
		if (_note.startT > startT && _note.startT < endT) {
			return true;
		} else if (_note.endT > startT && _note.endT < endT) {
			return true;
		} else if (_note.startT < startT && _note.endT > endT) {
			return true;
		}
		return false;
	}

	/**
	 * Determines whether or not the given note intersects this WINDOW OF TIME
	 * 
	 * @param _note
	 * @return
	 */
	public boolean intersects(float _startT, float _endT) {
		if (_startT > startT && _startT < endT) {
			return true;
		} else if (_endT > startT && _endT < endT) {
			return true;
		} else if (_startT >= startT && _endT <= endT) {
			return true;
		} else if (_startT <= startT && _endT >= endT) {
			return true;
		}
		return false;
	}

	public void moveByTime(float _changeInTime) {
		startT = startT + _changeInTime;
		endT = endT + _changeInTime;

		resizeBounds(false);
	}

	public void setBend(int _bend) {
		bend = _bend;
		relP = (idealRelP + (bend * K.CENTS_PER_SEMITONE));
		hz = K.hzFromRelP(project, relP);

		resizeBounds(false);
	}

	protected void setEndT(float _eT) {
//		TODO: create auto-move methods for intervals when note is moved

		endT = _eT;

		resizeBounds(false);
	}

	public void setPart(Part _part) {
		part = _part;
		project.repaint();
	}

	protected void setStartT(float _sT) {
		startT = _sT;

		resizeBounds(false);
	}

	public void showOvertones(boolean _showOvertones) {
		if (_showOvertones) {
			overtones = new Overtones(project, this);
			overtones.initAdd();

		} else if (overtones != null) {
			overtones.invalidateRemove(null);
			overtones = null;
		}
	}

	public void write(FileElement _e) throws CustomException {
		_e.add(BEND, bend);
		_e.add(GRAPH_ID, graph.id);
		_e.add(END_T, endT);
		_e.add(HZ, hz);
		_e.add(ID, id);
		_e.add(IDEAL_REL_P, idealRelP);
		_e.add(IS_COMPLETE, isComplete);
		_e.add(IS_SIMPLIFIED, isSimplified);
		_e.add(PART_NAME, part.name);
		_e.add(REL_P, relP);
		_e.add(START_T, startT);

		parentRatio.write(_e.createChild(PARENT_RATIO_TAG));
		ratio.write(_e.createChild(RATIO_TAG));

		boolean showOvertones = false;
		if (overtones != null) {
			showOvertones = true;
		}
		_e.add(SHOW_OVERTONES, showOvertones);
	}

	/**
	 * reverse completing of note.
	 * AUTO REPAINT
	 */
	void undoComplete() {
		if (!isComplete) {
			L.e("Note.complete()", "note is already incomplete!");
			return;
		}

		endT = startT;
		isComplete = false;

		for (ScoreWindow window : project.windowList) {
			window.resizeBounds(this, true);
		}
	}


	class CompleteNoteDrawable extends ScoreDrawable implements STYLE.NOTE {

		private int				bendLineY;
		private Polygon			startTriangle	= new Polygon();
		private Polygon			endTriangle		= new Polygon();

		private Rect			containsRect	= new Rect();
		private Rect			leftRect		= new Rect();
		private Rect			rightRect		= new Rect();


//		INTERVAL
		private final String	parentRatioText;
		private final String	ratioText;
		private Rect			intervalRect	= new Rect();
		private int				parentRatioY;
		private int				ratioY;


		private CompleteNoteDrawable(ScoreWindow _window, Note _note) {
			super(_window, _note, true);

			if (parentRatio.isUnison) {
				parentRatioText = "";
			} else {
				if (parentRatio.numerator > 9 || parentRatio.denominator > 9) {
					parentRatioText = parentRatio.numerator + ":" + parentRatio.denominator;
				} else {
					parentRatioText = parentRatio.numerator + "" + parentRatio.denominator;
				}
			}

			if (ratio.numerator > 9 || ratio.denominator > 9) {
				ratioText = ratio.numerator + ":" + ratio.denominator;
			} else if (ratio.isUnison && parentRatio.isUnison) {
				ratioText = "T";
			} else {
				ratioText = ratio.numerator + "" + ratio.denominator;
			}

			resize();
		}


		@Override
		public boolean contains(Point _point) {
			return containsRect.contains(_point);
		}

		@Override
		protected void createPopupMenu(MouseEvent _event) {
			NotePopupMenu popupMenu = new NotePopupMenu(window, (Note) scoreItem);
			popupMenu.show(window, _event.getX(), _event.getY());
		}

		@Override
		protected void draw(CustomGraphics _g) {
//		TODO: prevent drawing repeated intervals in same part & interval

			_g.fillRect(this, part.color);
			_g.line(left, bendLineY, right, bendLineY, BEND_LINE_COLOR);

			if (hasFocus) {
				_g.drawRect(this, FOCUSED_BORDER_COLOR);
				_g.fillPolygon(endTriangle, FOCUSED_BORDER_COLOR);
				_g.fillPolygon(startTriangle, FOCUSED_BORDER_COLOR);

			} else {
				_g.drawRect(this, INCOMPLETE_BORDER_COLOR);
				_g.fillPolygon(endTriangle, INCOMPLETE_BORDER_COLOR);
				_g.fillPolygon(startTriangle, INCOMPLETE_BORDER_COLOR);
			}

//			INTERVAL
//			TODO: fix interval background box drawing
			Color textColor;
			if (hasFocus) {
				textColor = FOCUSED_TEXT_COLOR;
			} else {
				textColor = RATIO_TEXT_COLOR;
			}

			_g.fillRect(intervalRect, RATIO_BACKGROUND_COLOR);

			_g.text(ratioText, intervalRect.centerX(), ratioY, CustomGraphics.Align.CENTER_CENTER, FONT, textColor);
			if (!isSimplified) {
				_g.text(parentRatioText, intervalRect.centerX(), parentRatioY, CustomGraphics.Align.CENTER_CENTER, FONT, textColor);
			}
		}

		void drawMultiSelect(CustomGraphics _g) {
			_g.drawRect(this, MULTI_SELECT_COLOR);
		}

		private EDIT_ZONE evalZone(Point _point) {
			if (leftRect.contains(_point)) {
				window.setCursor(CURSOR.HOZ);
				return EDIT_ZONE.START;

			} else if (rightRect.contains(_point)) {
				window.setCursor(CURSOR.HOZ);
				return EDIT_ZONE.END;

			} else if (contains(_point)) {
				window.setCursor(CURSOR.HAND);
				return EDIT_ZONE.CENTER;

			} else {
				window.setCursor(CURSOR.DEFAULT);
				return EDIT_ZONE.NONE;
			}
		}

		@Override
		protected void mouseDragged(Point _point) {
		}

		@Override
		protected void mouseEntered(Point _point) {
			evalZone(_point);
		}

		@Override
		protected void mouseExited(Point _point) {
			evalZone(_point);
		}

		@Override
		protected void mouseMoved(Point _point) {
			evalZone(_point);
		}

		@Override
		protected ActionItem mousePressed(Point _point) {
			EDIT_ZONE zone = evalZone(_point);
			if (zone == EDIT_ZONE.END || zone == EDIT_ZONE.START) {
				try {
					return new MoveNote(window, (Note) scoreItem, _point, zone);
				} catch (CustomException _exception) {
					_exception.print(true);
				}

			} else if (hasFocus) {
				if (KeyManager.has(KeyEvent.VK_ALT)) {
					return new BendNote(window, (Note) scoreItem, _point);

				} else if (KeyManager.has(KeyEvent.VK_CONTROL)) {
					try {
						return new CreateNote(window, graph, (Note) scoreItem, _point);

					} catch (CustomException _exception) {
						_exception.print(false);
					}
				}
			}

			return null;
		}

		@Override
		protected void mouseReleased(Point _point) {
		}

		@Override
		public void move(int _dX, int _dY) {
			super.move(_dX, _dY);

			int[] x = { left, (left + COMPLETE_TRIANGLE_SIZE), left };
			int[] y = { (centerY() + COMPLETE_TRIANGLE_SIZE), centerY(), (centerY() - COMPLETE_TRIANGLE_SIZE) };
			startTriangle = new Polygon(x, y, x.length);

			x = new int[] { right, (right - COMPLETE_TRIANGLE_SIZE), right };
			endTriangle = new Polygon(x, y, x.length);


			bendLineY = bendLineY + _dY;

			containsRect.move(_dX, _dY);
			leftRect.move(_dX, _dY);
			rightRect.move(_dX, _dY);


//			INTERVAL
			intervalRect.move(_dX, _dY);
			parentRatioY = parentRatioY + _dY;
			ratioY = ratioY + _dY;
		}

		@Override
		protected void resize() {
			int l = window.screenXFromTime(startT);
			int t = window.screenYFromRelP(idealRelP + COMPLETE_REL_P_HEIGHT);
			int r = window.screenXFromTime(endT);
			int b = window.screenYFromRelP(idealRelP - COMPLETE_REL_P_HEIGHT);
			set(l, t, r, b);

			bendLineY = window.screenYFromRelP(relP);

			int[] xArr = { left, (left + COMPLETE_TRIANGLE_SIZE), left };
			int[] yArr = { (centerY() + COMPLETE_TRIANGLE_SIZE), centerY(), (centerY() - COMPLETE_TRIANGLE_SIZE) };
			startTriangle = new Polygon(xArr, yArr, xArr.length);

			xArr = new int[] { right, (right - COMPLETE_TRIANGLE_SIZE), right };
			endTriangle = new Polygon(xArr, yArr, xArr.length);

			containsRect.set(left - CONTAINS_MARGIN, top, right + CONTAINS_MARGIN, bottom);
			leftRect.set(left - CONTAINS_MARGIN, top, left + CONTAINS_MARGIN, bottom);
			rightRect.set(right - CONTAINS_MARGIN, top, right + CONTAINS_MARGIN, bottom);


//			INTERVAL
			FontMetrics fontMetrics = window.getFontMetrics(FONT);

			int ascent = fontMetrics.getAscent();
			int height;
			if (isSimplified) {
				height = ascent;
			} else {
				height = (ascent * 2) + MARGIN_FROM_NOTE;
			}

			int width = fontMetrics.stringWidth(ratioText);
			if (!isSimplified && fontMetrics.stringWidth(parentRatioText) > width) {
				width = fontMetrics.stringWidth(parentRatioText);
			}

			int iL = 0, iR = 0, iT = 0, iB = 0;

			iL = left + MARGIN_FROM_NOTE;
			iR = iL + width;
			iT = window.screenYFromRelP(idealRelP - COMPLETE_REL_P_HEIGHT) - MARGIN_FROM_NOTE;
			iB = iT - height;

			intervalRect.set(iL, iT, iR, iB);

			if (isSimplified) {
				ratioY = intervalRect.centerY();
			} else {
				parentRatioY = intervalRect.bottom + (ascent / 2);
				ratioY = parentRatioY + ascent + INTERVAL_TEXT_MARGIN;
			}
		}

	}

	class IncompleteNoteDrawable extends ScoreDrawable implements STYLE.NOTE {


		private final String	parentRatioText;
		private final String	ratioText;

		private Polygon			diamond			= new Polygon();
		private Rect			intervalRect	= new Rect();

		private int				parentRatioY;
		private int				ratioY;


		public IncompleteNoteDrawable(ScoreWindow _window, ScoreItem _scoreItem) {
			super(_window, _scoreItem, true);

			if (parentRatio.isUnison) {
				parentRatioText = "";
			} else {
				if (parentRatio.numerator > 9 || parentRatio.denominator > 9) {
					parentRatioText = parentRatio.numerator + ":" + parentRatio.denominator;
				} else {
					parentRatioText = parentRatio.numerator + "" + parentRatio.denominator;
				}
			}

			if (ratio.numerator > 9 || ratio.denominator > 9) {
				ratioText = ratio.numerator + ":" + ratio.denominator;
			} else if (ratio.isUnison && parentRatio.isUnison) {
				ratioText = "T";
			} else {
				ratioText = ratio.numerator + "" + ratio.denominator;
			}

			resize();
		}


		@Override
		public boolean contains(Point _point) {
			return diamond.contains(_point);
		}

		@Override
		protected void draw(CustomGraphics _g) {
			_g.fillPolygon(diamond, part.color);

			if (hasFocus) {
				_g.drawPolygon(diamond, FOCUSED_BORDER_COLOR);
			} else {
				_g.drawPolygon(diamond, INCOMPLETE_BORDER_COLOR);
			}


//			INTERVAL
			Color textColor;
			if (hasFocus) {
				textColor = FOCUSED_TEXT_COLOR;
			} else {
				textColor = RATIO_TEXT_COLOR;
			}

			_g.fillRect(intervalRect, RATIO_BACKGROUND_COLOR);

			_g.text(ratioText, intervalRect.centerX(), ratioY, CustomGraphics.Align.CENTER_CENTER, FONT, textColor);
			if (!isSimplified) {
				_g.text(parentRatioText, intervalRect.centerX(), parentRatioY, CustomGraphics.Align.CENTER_CENTER, FONT, textColor);
			}
		}

		@Override
		protected void mouseDragged(Point _point) {
		}

		@Override
		protected void mouseEntered(Point _point) {
			window.setCursor(CURSOR.HAND);
		}

		@Override
		protected void mouseExited(Point _point) {
			window.setCursor(CURSOR.DEFAULT);
		}

		@Override
		protected void mouseMoved(Point _point) {
		}

		@Override
		protected ActionItem mousePressed(Point _point) {
			if (hasFocus) {
				try {
					return new CompleteNote(window, (Note) scoreItem, _point);
				} catch (CustomException exception) {
					exception.print(false);
				}
			}
			return null;
		}

		@Override
		protected void mouseReleased(Point _point) {
		}

		@Override
		public void move(int _dX, int _dY) {
			resize();
		}

		@Override
		protected void resize() {
			int cX = window.screenXFromTime(startT);
			int cY = window.screenYFromRelP(relP);

			set((cX - INCOMPLETE_SIZE), (cY + INCOMPLETE_SIZE), (cX + INCOMPLETE_SIZE), (cY - INCOMPLETE_SIZE));

			int[] xArr = { left, cX, right, cX };
			int[] yArr = { cY, top, cY, bottom };
			diamond = new Polygon(xArr, yArr, xArr.length);


//			INTERVAL
			FontMetrics fontMetrics = window.getFontMetrics(FONT);

			int ascent = fontMetrics.getAscent();
			int intervalHeight;
			if (isSimplified) {
				intervalHeight = ascent;
			} else {
				intervalHeight = (ascent * 2) + INCOMPLETE_MARGIN_FROM_NOTE;
			}

			int intervalWidth = fontMetrics.stringWidth(ratioText);
			if (!isSimplified && fontMetrics.stringWidth(parentRatioText) > intervalWidth) {
				intervalWidth = fontMetrics.stringWidth(parentRatioText);
			}

			int iL = 0, iR = 0, iT = 0, iB = 0;

			iL = left + INCOMPLETE_MARGIN_FROM_NOTE;
			iR = iL + intervalWidth;
			iT = window.screenYFromRelP(idealRelP) - INCOMPLETE_MARGIN_FROM_NOTE;
			iB = iT - intervalHeight;

			intervalRect.set(iL, iT, iR, iB);

			if (isSimplified) {
				ratioY = intervalRect.centerY();
			} else {
				parentRatioY = intervalRect.bottom + (ascent / 2);
				ratioY = parentRatioY + ascent + INTERVAL_TEXT_MARGIN;
			}
		}

	}

}
