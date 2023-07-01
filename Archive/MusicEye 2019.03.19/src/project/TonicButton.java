package project;

import java.awt.Color;
import java.awt.Point;

import file.FILE;
import file.FileElement;
import gui.CustomGraphics;
import gui.CustomGraphics.Align;
import gui.STYLE;
import gui.ScoreWindow;
import note.CreateNote;
import util.CustomException;

public class TonicButton extends ScoreItem implements FILE.TONIC_BUTTON_F {

	static public TonicButton read(Project _project, FileElement _e) throws CustomException {
		int octaveNumber = _e.get(OCTAVE_NUMBER);
		float relP = _e.get(REL_P);
		
		for(Graph graph: _project.graphList) {
			if(graph.id == _e.get(GRAPH_ID)) {
				return new TonicButton(_project, graph, octaveNumber, relP);
			}
		}
		throw new CustomException("TonicButton.read");
	}


	private final Graph	graph;
	private final int	octaveNumber;
	private float		relP;
	private String		text;


	public TonicButton(Project _project, Graph _graph, int _octaveNumber, float _relP) {
		super(Type.TONIC_BUTTON, _project);

		graph = _graph;
		octaveNumber = _octaveNumber;
		relP = _relP;
	}


	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		return new TonicButtonDrawable(_window, this);
	}

	@Override
	protected Bounds createBounds(ScoreWindow _window) {

		class TonicButtonBounds extends Bounds {

			private TonicButtonBounds(TonicButton _tonicButton, ScoreWindow _window) {
				super(_tonicButton, _window);
			}

			@Override
			protected boolean checkVisibility() {
				return window.visibleRange.intersects(graph.startTime, relP, graph.endTime);
			}
		}

		return new TonicButtonBounds(this, _window);
	}

	public void write(FileElement _d) throws CustomException {
		_d.add(GRAPH_ID, graph.id);
		_d.add(OCTAVE_NUMBER, octaveNumber);
		_d.add(REL_P, relP);
	}


	private class TonicButtonDrawable extends ScoreDrawable implements STYLE.TONIC_BUTTON {

		private int	centerX;
		private int	centerY;


		private TonicButtonDrawable(ScoreWindow _window, TonicButton _tonicButton) {
			super(_window, _tonicButton, false);
			resize();
		}


		@Override
		protected void draw(CustomGraphics _g) {
			Color borderColor;
			Color textColor;
			if (hasFocus) {
				borderColor = FOCUSED_BORDER_COLOR;
				textColor = FOCUSED_TEXT_COLOR;
			} else {
				borderColor = BORDER_COLOR;
				textColor = TEXT_COLOR;
			}

			_g.fillCircle(centerX, centerY, RADIUS, FILL_COLOR);
			_g.drawCircle(centerX, centerY, RADIUS, borderColor, STROKE.BASIC_2);

			if (octaveNumber == 0) {
				_g.text(text, centerX, (centerY - TEXT_CENTER_BALANCE), Align.CENTER_CENTER, PRIME_FONT, textColor);
			} else {
				_g.text(text, (centerX - TEXT_CENTER_BALANCE), centerY, Align.CENTER_CENTER, FONT, textColor);
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
			try {
				return new CreateNote(window, graph, relP, _point);
			} catch (CustomException exception) {
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
			int startMargin = (-window.graphFrame.left - STYLE.GRAPH.START_LINES_D_X);
			int buttonPlusMargins = ((RADIUS * 2) + (X_MARGIN * 2));

			if (buttonPlusMargins > startMargin) {
				centerX = (X_MARGIN + RADIUS);
			} else {
				centerX = (startMargin - X_MARGIN - RADIUS);
			}

			centerY = window.screenYFromRelP(relP);

			set(centerX - RADIUS, centerY + RADIUS, centerX + RADIUS, centerY - RADIUS);

			if (octaveNumber > 0) {
				text = "+" + octaveNumber;
			} else if (octaveNumber == 0) {
				text = TONIC_TEXT;
			} else {
				text = Integer.toString(octaveNumber);
			}
		}

	}

}
