package project;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

import file.FILE;
import file.FileElement;
import gui.CustomGraphics;
import gui.STYLE;
import gui.ScoreWindow;
import util.CustomException;
import util.L;

public class TimeMarker extends ScoreItem implements FILE.TIME_MARKER_F {

	static public TimeMarker read(Project _project, FileElement _e) throws CustomException {
		float time = _e.get(TIME);
		for (Graph g : _project.graphList) {
			if (time >= g.startTime && time <= g.endTime) {
				return new TimeMarker(_project, g, time);
			}
		}

		L.e("TimeMarker.read", "No timeMarker found!");
		if (_project.graphList.size() == 0) {
			return new TimeMarker(_project, _project.graphList.get(0));
		} else {
			L.e("TimeMarker.read", "No graph in graphList!!!");
			return null;
		}
	}


	private Graph	containingGraph;
	private float	time	= 0;


	public TimeMarker(Project _project, Graph _containingGraph) {
		super(Type.TIME_MARKER, _project);
		containingGraph = _containingGraph;
	}

	public TimeMarker(Project _project, Graph _containingGraph, float _time) {
		super(Type.TIME_MARKER, _project);
		containingGraph = _containingGraph;
		time = _time;
	}


	@Override
	protected Bounds createBounds(ScoreWindow _window) {

		class TimeMarkerBounds extends Bounds {

			private TimeMarkerBounds(TimeMarker _timeMarker, ScoreWindow _window) {
				super(_timeMarker, _window);
			}

			@Override
			protected boolean checkVisibility() {
				return window.visibleRange.intersects(time);
			}
		}

		return new TimeMarkerBounds(this, _window);
	}

	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		return new TimeMarkerDrawable(_window, this);
	}

	public void write(FileElement _d) throws CustomException {
		_d.add(CONTAINING_GRAPH_ID, containingGraph.id);
		_d.add(TIME, time);
	}


	private class TimeMarkerDrawable extends ScoreDrawable implements STYLE.TIME_MARKER {

		private Polygon topTriangle, bottomTriangle;


		private TimeMarkerDrawable(ScoreWindow _window, TimeMarker _timeMarker) {
			super(_window, _timeMarker, true);
			resize();
		}


		@Override
		public boolean contains(Point _point) {
			return topTriangle.contains(_point);
		}

		@Override
		protected void draw(CustomGraphics _g) {
			Color borderColor;
			if (hasFocus) {
				borderColor = FOCUSED_BORDER_COLOR;
			} else {
				borderColor = BORDER_COLOR;
			}

			_g.line(left, top, left, bottom, borderColor);

			_g.fillPolygon(topTriangle, FILL_COLOR);
			_g.fillPolygon(bottomTriangle, FILL_COLOR);

			_g.drawPolygon(topTriangle, borderColor);
			_g.drawPolygon(bottomTriangle, borderColor);
		}

		@Override
		protected void mouseDragged(Point _point) {
			float tempTime = window.timeFromScreenX(_point.x);
			if (tempTime < 0) {
				tempTime = 0;
			}
			time = tempTime;
			resize();
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
			time = window.timeFromScreenX(_point.x);
			resize();
			return null;
		}

		@Override
		protected void mouseReleased(Point _point) {
			float tempTime = window.timeFromScreenX(_point.x);
			if (tempTime < 0) {
				tempTime = 0;
			}
			time = tempTime;
			resize();
		}

		@Override
		public void move(int _dX, int _dY) {
			super.move(_dX, 0);

			topTriangle.translate(_dX, 0);
			bottomTriangle.translate(_dX, 0);
		}

		@Override
		protected void resize() {
			int l = window.screenXFromTime(time);
			int t = window.drawRect.top;
			int r = l;
			int b = window.drawRect.bottom;
			set(l, t, r, b);

			int[] xList = { (left - TOP_TRIANGLE_SIZE), (left + TOP_TRIANGLE_SIZE), left };
			int[] yT = { top, top, (top - TOP_TRIANGLE_SIZE) };
			topTriangle = new Polygon(xList, yT, xList.length);

			xList = new int[] { (left - BOTTOM_TRIANGLE_SIZE), (left + BOTTOM_TRIANGLE_SIZE), left };
			int[] yB = { bottom, bottom, (bottom + BOTTOM_TRIANGLE_SIZE) };
			bottomTriangle = new Polygon(xList, yB, xList.length);
		}

	}

}
