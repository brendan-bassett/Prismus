package sound;

import java.awt.Point;
import java.awt.Polygon;

import gui.CustomGraphics;
import gui.STYLE;
import gui.ScoreWindow;
import project.ActionItem;
import project.Bounds;
import project.Project;
import project.ScoreDrawable;
import project.ScoreItem;

public class PlayMarker extends ScoreItem {

	class PlayingAction extends ActionItem implements STYLE.PLAY_MARKER {

		int playingX;


		PlayingAction(ScoreWindow _window) {
			super(_window);

			resize();
		}


		@Override
		public void draw(CustomGraphics _g) {
			_g.line(playingX, window.drawRect.top, playingX, window.drawRect.bottom, LINE_COLOR, LINE_STROKE);
		}

		@Override
		public void mouseDragged(Point _point) {
		}

		@Override
		public void mouseReleased(Point _point) {
		}

		@Override
		public void move(int _dX, int _dY) {
			playingX = playingX + _dX;
		}

		@Override
		public void resize() {
			playingX = window.screenXFromTime(soundMenu.playingTime);
			if (window == project.focusedWindow) {
				if (window.graphFrame.right < window.rightLimit()) {
					int windowMoveX = window.drawRect.width - WINDOW_MOVE_RIGHT;
					if (playingX > windowMoveX) {
						int dX = -(window.drawRect.width - WINDOW_MOVE_RIGHT - WINDOW_MOVE_LEFT);
						window.moveContents(dX, 0);
					}
				}
			}
		}

		@Override
		public void cancel() {
			window.removeActionItem();
		}

	}

	class PlayMarkerDrawable extends ScoreDrawable implements STYLE.PLAY_MARKER {

		Polygon	topTriangle;
		int		resetX;


		public PlayMarkerDrawable(ScoreWindow _window, ScoreItem _scoreItem) {
			super(_window, _scoreItem, false);

			resize();
		}

		@Override
		protected void draw(CustomGraphics _g) {
			_g.line(resetX, window.drawRect.top, resetX, window.drawRect.bottom, LINE_COLOR, LINE_STROKE);
			_g.fillPolygon(topTriangle, TOP_TRIANGLE_FILL);
			_g.drawPolygon(topTriangle, LINE_COLOR, TOP_TRIANGLE_STROKE);
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
			resetX = resetX + _dX;
			topTriangle.translate(_dX, 0);
		}

		@Override
		protected void resize() {
			resetX = window.screenXFromTime(soundMenu.playingTime);

			int[] x = new int[] { (resetX - TOP_TRIANGLE_SIZE), resetX, (resetX + TOP_TRIANGLE_SIZE) };
			int[] y = new int[] { window.drawRect.top, (window.drawRect.top - TOP_TRIANGLE_SIZE), window.drawRect.top };
			topTriangle = new Polygon(x, y, x.length);
		}
	}


	private final SoundMenu soundMenu;


	public PlayMarker(Project _project, SoundMenu _soundMenu) {
		super(Type.PLAY_MARKER, _project);

		soundMenu = _soundMenu;
	}


	@Override
	public ScoreDrawable createDrawable(ScoreWindow _window) {
		return new PlayMarkerDrawable(_window, this);
	}


	@Override
	protected Bounds createBounds(ScoreWindow _window) {
		class PlayMarkerBounds extends Bounds {

			PlayMarkerBounds(PlayMarker _playMarker, ScoreWindow _window) {
				super(_playMarker, _window);
			}


			@Override
			protected boolean checkVisibility() {
				return window.visibleRange.intersects(soundMenu.resetTime);
			}

		}

		return new PlayMarkerBounds(this, _window);
	}

	void startPlayingAction() {
		for (ScoreWindow window : project.windowList) {
			PlayingAction playingAction = new PlayingAction(window);
			soundMenu.playingActionList.add(playingAction);
			window.setActionItem(playingAction);
		}
	}

}
