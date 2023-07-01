package gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import file.FILE;
import file.FileElement;
import note.Note;
import project.ActionItem;
import project.Bounds;
import project.Project;
import project.ScoreItem;
import project.ScoreItem.Type;
import util.CustomException;
import util.L;

public class ScoreWindow extends DrawWindow implements FILE.SCORE_WINDOW_F, STYLE.SCORE_WINDOW {

	static private final long	serialVersionUID	= 7182987152010698143L;


	static public ScoreWindow read(Project _project, FileElement _e) throws CustomException {
		float ppo = _e.get(PPO);
		float pps = _e.get(PPS);
		Rect graphWindow = Rect.read(_e.getChild(GRAPH_BOUNDS));

		return new ScoreWindow(_project, ppo, pps, graphWindow);
	}


	final Rect						boundsFrame		= new Rect(	-DRAW_MARGIN,
																(DEFAULT_HEIGHT + DRAW_MARGIN),
																(DEFAULT_WIDTH + DRAW_MARGIN),
																-DRAW_MARGIN);
	public final Rect				graphFrame;

	public final VisibleRange		visibleRange;												// Range which the window shows in RELP and TIME
	private final ArrayList<Bounds>	boundsList;

	float							ppo				= PPO_INIT;									// "Pixels per Octave"
	float							pps				= PPS_INIT;									// "Pixels per Second"

	final WindowListener			windowListener;
	final NavBar					hNavBar;
	final NavBar					vNavBar;

	ActionItem						actionItem		= null;

	private Cursor					currentCursor	= CURSOR.DEFAULT;


	public ScoreWindow(Project _project) {
		super(_project, new Rect(0, DEFAULT_HEIGHT, DEFAULT_WIDTH, 0));

//		TODO: refine Window & NavBar resizing
//		TODO: organize window repainting / resizing (specifically with overtones)

		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setFocusable(true);
		this.grabFocus();

		int l = -SCORE_START_MARGIN;
		int r = (l + DEFAULT_WIDTH);
		int b = pixelsFromRelP(INIT_REL_P);
		int t = (b + DEFAULT_HEIGHT);
		graphFrame = new Rect(l, t, r, b);

		hNavBar = new NavBar(this, NavBar.Orientation.HOZ);
		vNavBar = new NavBar(this, NavBar.Orientation.VERT);

		visibleRange = new VisibleRange(this);
		visibleRange.update();
		boundsList = project.initBounds(this);

		windowListener = new WindowListener(this);
		this.addComponentListener(windowListener);
		this.addMouseListener(windowListener);
		this.addMouseMotionListener(windowListener);
		this.addMouseWheelListener(windowListener);

		this.setCursor(currentCursor);
	}

	public ScoreWindow(Project _project, float _ppo, float _pps, Rect _graphWindow) {
		super(_project, new Rect(0, DEFAULT_HEIGHT, DEFAULT_WIDTH, 0));

		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setFocusable(true);
		this.grabFocus();

		ppo = _ppo;
		pps = _pps;
		graphFrame = _graphWindow;

		hNavBar = new NavBar(this, NavBar.Orientation.HOZ);
		vNavBar = new NavBar(this, NavBar.Orientation.VERT);

		visibleRange = new VisibleRange(this);
		visibleRange.update();
		boundsList = project.initBounds(this);

		windowListener = new WindowListener(this);
		this.addComponentListener(windowListener);
		this.addMouseListener(windowListener);
		this.addMouseMotionListener(windowListener);
		this.addMouseWheelListener(windowListener);

		this.setCursor(currentCursor);
	}


	/**
	 * @param _bounds
	 */
	public void addBounds(Bounds _bounds) {
		boundsList.add(_bounds);
	}

	public int bottomLimit() {
		return bottomLimit(ppo);
	}

	private int bottomLimit(float _ppo) {
		return Math.round(project.bottomRelP * _ppo);
	}

	public void centerOnRelP(float _relP) {
		float centerRelP = relPFromScreenY(boundsFrame.centerY());
		float difRelP = (_relP - centerRelP);
		int dY = Math.round(difRelP * ppo);
		moveContents(0, -dY);
	}

	public void centerOnTime(float _time) {
		float centerTime = timeFromScreenX(boundsFrame.centerX());
		float difTime = (_time - centerTime);
		int dX = Math.round(difTime * pps);
		moveContents(-dX, 0);
	}

	public ArrayList<Bounds> getBoundsList() {
		return boundsList;
	}

	public Bounds getFocusedBounds() {
		return windowListener.focusedBounds;
	}
	
	ArrayList<Bounds> getVisibleBounds() {
		ArrayList<Bounds> visibleBounds = new ArrayList<Bounds>();
		for (Bounds bounds : boundsList) {
			if (bounds.isVisible) {
				visibleBounds.add(bounds);
			}
		}

		return visibleBounds;
	}

	/**
	 * @return all notes currently visible within this window
	 */
	public ArrayList<Note> getVisibleNotes() {
		ArrayList<Note> visibleNotes = new ArrayList<Note>();
		ArrayList<Bounds> visibleBounds = getVisibleBounds();

		for (Bounds bounds : visibleBounds) {
			if (bounds.scoreItem.type == ScoreItem.Type.NOTE) {
				visibleNotes.add((Note) bounds.scoreItem);
			}
		}

		return visibleNotes;
	}

	public void giveFocus(ScoreItem _scoreItem) {
		windowListener.giveFocus(_scoreItem);
	}

	public void invalidateBounds(ScoreItem _scoreItem, ScoreItem _newFocusedSI) {
		for (Bounds bounds : boundsList) {
			if (bounds.scoreItem == _scoreItem) {

				boolean success = boundsList.remove(bounds);
				if (success) {
					L.l("Window.invalidateBounds", "success");

					if (bounds == windowListener.focusedBounds) {

						if (_newFocusedSI == null) {
							windowListener.removeFocus();
						} else {
							for (Bounds newFocusedBounds : boundsList) {
								if (newFocusedBounds.scoreItem == _newFocusedSI) {
									windowListener.giveFocus(_newFocusedSI);
								}
							}
						}
					}
				} else {
					L.e("Window.invalidateBounds", "!success");
				}

				return;
			}
		}
		L.e("Window.invalidateBounds()", "no bounds.scoreItem in boundsList was == _scoreItem");
	}

	public int invertScreenY(int _inputY) {
		return Math.round(drawRect.height - _inputY);
	}

	public int leftLimit() {
		return -SCORE_START_MARGIN;
	}

	public void moveContents(int _dX, int _dY) {
		int newDX = _dX;
		int newDY = _dY;

		int gWindowL = (graphFrame.left - newDX);
		int gWindowR = (graphFrame.right - newDX);
		int gWindowB = (graphFrame.bottom - newDY);
		int gWindowT = (graphFrame.top - newDY);

		int leftLimit = leftLimit();
		int topLimit = topLimit(ppo);
		int rightLimit = rightLimit(pps);
		int bottomLimit = bottomLimit(ppo);

		if (gWindowL < leftLimit) {
			gWindowL = leftLimit;
			gWindowR = (gWindowL + graphFrame.width);
			newDX = (graphFrame.left - gWindowL);
		} else if (gWindowR > rightLimit) {
			gWindowR = rightLimit;
			gWindowL = (gWindowR - graphFrame.width);
			newDX = (graphFrame.right - gWindowR);
		}

		if (gWindowB < bottomLimit) {
			gWindowB = bottomLimit;
			gWindowT = (gWindowB + graphFrame.height);
			newDY = (graphFrame.bottom - gWindowB);
		} else if (gWindowT > topLimit) {
			gWindowT = topLimit;
			gWindowB = (gWindowT - graphFrame.height);
			newDY = (graphFrame.top - topLimit);
		}

		graphFrame.move(-newDX, -newDY);
		visibleRange.update();

		hNavBar.resize();
		vNavBar.resize();
		hNavBar.repaint();
		vNavBar.repaint();

		if (actionItem != null) {
			actionItem.move(newDX, newDY);
		}

		for (Bounds bounds : boundsList) {
			bounds.move(newDX, newDY);
		}
	}

	@Override
	public void paintComponent(Graphics _graphics) {
		super.paintComponent(_graphics);
		CustomGraphics customGraphics = new CustomGraphics(this, (Graphics2D) _graphics, drawRect);

		for (Type type : ScoreItem.TYPE_LIST) {
			for (Bounds bounds : boundsList) {
				if (bounds.scoreItem.type == type && bounds != windowListener.focusedBounds) {
					bounds.draw(customGraphics);
				}
			}
		}

		if (windowListener.focusedBounds != null) {
			windowListener.focusedBounds.draw(customGraphics);
		}

		if (actionItem != null) {
			actionItem.draw(customGraphics);
		}
	}

	public int pixelsFromRelP(float _relP) {
		return Math.round(_relP * ppo);
	}

	public int pixelsFromTime(float _time) {
		return Math.abs(Math.round(_time * pps));
	}

	public float relPFromPixels(int _pixels) {
		return (_pixels / ppo);
	}

	public float relPFromScreenY(int _screenY) {
		return ((_screenY + graphFrame.bottom) / ppo);
	}

	public void removeActionItem() {
		actionItem = null;
	}

	public void resizeAllBounds(boolean _remakeDrawables) {
		for (Bounds bounds : boundsList) {
			bounds.resize(_remakeDrawables);
		}
		repaint();
	}

	/**
	 * resize only the bounds corresponding to the given ScoreItem.
	 * AUTO REPAINT
	 * 
	 * @param _scoreItem
	 * @param _remakeDrawable
	 */
	public void resizeBounds(ScoreItem _scoreItem, boolean _remakeDrawable) {
		for (Bounds bounds : boundsList) {
			if (bounds.scoreItem == _scoreItem) {
				bounds.resize(_remakeDrawable);
				repaint();
				return;
			}
		}
		L.e("Window.invalidateBounds", "no bounds.scoreItem in boundsList is == _scoreItem");
	}

	public int rightLimit() {
		return rightLimit(pps);
	}

	private int rightLimit(float _pps) {
		return Math.round(project.getEndTime() * _pps);
	}

	public int screenXFromTime(float _time) {
		return screenXFromTime(_time, pps);
	}

	private int screenXFromTime(float _time, float _pps) {
		return Math.round((_time * _pps) - graphFrame.left);
	}

	public int screenYFromRelP(float _relP) {
		return screenYFromRelP(_relP, ppo);
	}

	private int screenYFromRelP(float _relP, float _ppo) {
		return Math.round((_ppo * _relP) - graphFrame.bottom);
	}

	public void setActionItem(ActionItem _actionItem) {
		if (actionItem != null) {
			actionItem.cancel();
		}
		actionItem = _actionItem;
	}

	@Override
	public void setCursor(Cursor _cursor) {
		if (currentCursor != _cursor) {
			super.setCursor(_cursor);
			currentCursor = _cursor;
		}
	}

	public float timeFromPixels(int _pixels) {
		return (_pixels / pps);
	}

	public float timeFromScreenX(int _screenX) {
		return ((_screenX + graphFrame.left) / pps);
	}

	public int topLimit() {
		return topLimit(ppo);
	}

	private int topLimit(float _ppo) {
		return Math.round(project.topRelP * _ppo);
	}

	public void write(FileElement _e) throws CustomException {
		_e.add(PPO, ppo);
		_e.add(PPS, pps);
		graphFrame.write(_e.createChild(GRAPH_BOUNDS));
	}

	public void xZoom(float _zoomRatio) {
		xZoom(_zoomRatio, boundsFrame.centerX());
	}

	public void xZoom(float _zoomRatio, int _zoomCenterX) {
		float newPPS = Math.round(pps * _zoomRatio);

		if (newPPS > PPS_MAX) {
			if (pps == PPS_MAX) {
				return;
			} else {
				newPPS = PPS_MAX;
			}
		} else if (newPPS < PPS_MIN) {
			if (pps == PPS_MIN) {
				return;
			} else {
				newPPS = PPS_MIN;
			}
		}

		int lLimit = leftLimit();
		int rLimit = rightLimit(newPPS);
		if ((rLimit - lLimit) < drawRect.width) {
			if ((rightLimit(pps) - leftLimit()) == drawRect.width) {
				return;
			} else {
				newPPS = ((float) drawRect.width - SCORE_START_MARGIN) / project.getEndTime();
				rLimit = rightLimit(newPPS);
			}
		}

		float zoomCenterT = timeFromScreenX(_zoomCenterX);
		int newZoomCenterX = screenXFromTime(zoomCenterT, newPPS);
		int zoomCenterDifference = (newZoomCenterX - _zoomCenterX);
		int newGraphLeft = (graphFrame.left + zoomCenterDifference);
		int newGraphRight = (graphFrame.right + zoomCenterDifference);

		if (newGraphLeft < lLimit) {
			newGraphLeft = lLimit;
			newGraphRight = newGraphLeft + drawRect.width;
		}
		if (newGraphRight > rLimit) {
			newGraphRight = rLimit;
			newGraphLeft = newGraphRight - drawRect.width;
		}

		pps = newPPS;
		graphFrame.set(newGraphLeft, graphFrame.top, newGraphRight, graphFrame.bottom);
		visibleRange.update();

		for (Bounds bounds : boundsList) {
			bounds.resize(false);
		}

		hNavBar.repaint();
		hNavBar.resize();
	}

	public void yZoom(float _zoomRatio) {
		yZoom(_zoomRatio, boundsFrame.centerY());
	}

	public void yZoom(float _zoomRatio, int _zoomCenterY) {
		float newPPO = ppo * _zoomRatio;

		if (newPPO > PPO_MAX) {
			if (ppo == PPO_MAX) {
				return;
			} else {
				newPPO = PPO_MAX;
			}
		} else if (newPPO < PPO_MIN) {
			if (ppo == PPO_MIN) {
				return;
			} else {
				newPPO = PPO_MIN;
			}
		}

		int bLimit = bottomLimit(newPPO);
		int tLimit = topLimit(newPPO);
		if ((tLimit - bLimit) < graphFrame.height) {
			if ((topLimit(ppo) - bottomLimit(ppo)) == graphFrame.height) {
				return;
			} else {
				newPPO = (float) graphFrame.height / (project.topRelP - project.bottomRelP);
				bLimit = bottomLimit(newPPO);
				tLimit = topLimit(newPPO);
			}
		}

		float zoomCenterRelP = relPFromScreenY(_zoomCenterY);
		int newZoomCenterY = screenYFromRelP(zoomCenterRelP, newPPO);
		int zoomCenterDifference = (newZoomCenterY - _zoomCenterY);

		int newGraphBottom = (graphFrame.bottom + zoomCenterDifference);
		int newGraphTop = (graphFrame.top + zoomCenterDifference);

		if (newGraphBottom < bLimit) {
			newGraphBottom = bLimit;
			newGraphTop = newGraphBottom + graphFrame.height;
		}
		if (newGraphTop > tLimit) {
			newGraphTop = tLimit;
			newGraphBottom = newGraphTop - graphFrame.height;
		}

		ppo = newPPO;
		graphFrame.set(graphFrame.left, newGraphTop, graphFrame.right, newGraphBottom);
		visibleRange.update();

		for (Bounds bounds : boundsList) {
			bounds.resize(false);
		}

		vNavBar.repaint();
		vNavBar.resize();
	}

}
