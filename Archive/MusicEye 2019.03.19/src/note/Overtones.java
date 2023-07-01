package note;

import java.awt.Point;

import gui.CustomGraphics;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;
import project.Bounds;
import project.Project;
import project.ScoreDrawable;
import project.ScoreItem;
import util.L;
import util.K;


public class Overtones extends ScoreItem implements STYLE.OVERTONES {

	private class OvertonesDrawable extends ScoreDrawable {

		private final int[] overtoneYList = new int[OVERTONES_SHOWN];


		private OvertonesDrawable(ScoreWindow _window, ScoreItem _scoreItem) {
			super(_window, _scoreItem, false);

			this.resize();
		}


		@Override
		public boolean contains(Point _point) {
			return false;
		}

		@Override
		protected void draw(CustomGraphics _g) {
			if (overtoneYList != null) {
				for (int i = 1; i < overtoneYList.length; i++) {
					_g.line(left, overtoneYList[i], right, overtoneYList[i], COLOR);
				}
			}

//			L.l("Overtones.draw()", "-------------------------- Y list Length == " + overtoneYList.length);
//			for(int i=1; i<overtoneYList.length; i++) {
//				L.l("Overtones.draw()", "i==" + i + " y==" + overtoneYList[i]);
//			}
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
			if (overtoneYList != null) {
				for (int i = 0; i < overtoneYList.length; i++) {
					overtoneYList[i] = overtoneYList[i] + _dY;
				}
			}
		}

		@Override
		protected void resize() {
			L.l("Overtones.resize");

			for (int i = 0; i < OVERTONES_SHOWN; i++) {
				overtoneYList[i] = window.screenYFromRelP(overtoneRelPList[i]);
			}

			int l = window.screenXFromTime(note.startT);
			int t = window.screenYFromRelP(overtoneRelPList[OVERTONES_SHOWN - 1]);
			int r = window.screenXFromTime(note.endT);
			int b = window.screenYFromRelP(note.relP);
			set(l, t, r, b);
		}

	}


	private final Note		note;
	private final float[]	overtoneRelPList	= new float[OVERTONES_SHOWN];


	Overtones(Project _project, Note _note) {
		super(Type.OVERTONES, _project);

		note = _note;

		for (int i = 0; i < OVERTONES_SHOWN; i++) {
			overtoneRelPList[i] = K.relPFromHz(project, note.hz * (i + 1));
		}
	}


	@Override
	protected Bounds createBounds(ScoreWindow _window) {

		class OvertonesBounds extends Bounds {

			private OvertonesBounds(Overtones _overtones, ScoreWindow _window) {
				super(_overtones, _window);
			}

			@Override
			protected boolean checkVisibility() {
				return window.visibleRange.intersects(note.startT, overtoneRelPList[OVERTONES_SHOWN - 1], note.endT, note.relP);
			}
		}

		return new OvertonesBounds(this, _window);
	}

	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		return new OvertonesDrawable(_window, this);
	}

}
