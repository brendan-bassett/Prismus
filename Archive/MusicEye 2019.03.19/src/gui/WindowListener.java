package gui;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.event.MouseInputListener;

import note.MultiSelect;
import note.Note;
import project.ActionItem;
import project.Bounds;
import project.ScoreItem;
import project.ScoreItem.Type;
import util.L;


class WindowListener implements ComponentListener, MouseInputListener, MouseWheelListener, STYLE.SCORE_WINDOW {

	Bounds				focusedBounds		= null;

	private ScoreWindow	window;

	private Bounds		hoveredBounds		= null;
	private boolean		isMoving			= false;

	private boolean		mouseLeftIsPressed	= false;
	private boolean		mouseRightIsPressed	= false;
	private float		mouseWheel			= 0;

	private Point		prevMovePoint		= new Point();


	WindowListener(ScoreWindow _window) {
		window = _window;
	}


	@Override
	public void componentHidden(ComponentEvent _event) {
	}

	@Override
	public void componentMoved(ComponentEvent _event) {
	}

	@Override
	public void componentResized(ComponentEvent _event) {
		int l = window.graphFrame.left;
		int t = window.graphFrame.top;
		int r = (l + window.getWidth());
		int b = (t - window.getHeight());
		window.graphFrame.set(l, t, r, b);

		window.drawRect.set(0, window.graphFrame.height, window.graphFrame.width, 0);
		window.boundsFrame
				.set(-DRAW_MARGIN, (window.graphFrame.height + DRAW_MARGIN), (window.graphFrame.width + DRAW_MARGIN), -DRAW_MARGIN);

		window.resizeAllBounds(false);
	}

	@Override
	public void componentShown(ComponentEvent _event) {
	}

	void giveFocus(ScoreItem _scoreItem) {
		for (Bounds bounds : window.getBoundsList()) {
			if (bounds.scoreItem == _scoreItem) {
				focusedBounds.setHasFocus(false);
				focusedBounds = bounds;
				bounds.setHasFocus(true);

				if (bounds.scoreItem.type == ScoreItem.Type.NOTE) {
					window.project.intervalWindow.updateNote((Note) bounds.scoreItem);
				}
				return;
			}
		}
		L.e("Window.giveFocus()", "_scoreItem did not match b.scoreItem in any bounds");
	}

	public void keyPressed(KeyEvent _event) {
		if (window.actionItem != null) {
			window.actionItem.keyPressed(_event);
			window.repaint();
			return;
		}

		if (focusedBounds != null) {
			focusedBounds.keyPressed(_event);
			window.repaint();
			return;
		}
	}

	public void keyReleased(KeyEvent _event) {
		if (window.actionItem != null) {
			window.actionItem.keyReleased(_event);
			window.repaint();
			return;
		}

		if (focusedBounds != null) {
			window.repaint();
			return;
		}
	}

	@Override
	public void mouseClicked(MouseEvent _event) {
	}

	@Override
	public void mouseDragged(MouseEvent _event) {
		Point point = new Point(_event.getX(), window.invertScreenY(_event.getY()));

		if (isMoving) {
			int dX = (point.x - prevMovePoint.x);
			int dY = (point.y - prevMovePoint.y);
			window.moveContents(dX, dY);
			prevMovePoint = point;

		} else {
			if (window.actionItem != null) {
				window.actionItem.mouseDragged(point);

			} else if (focusedBounds != null) {
				focusedBounds.mouseDragged(point);
			}
		}

		window.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent _event) {
	}

	@Override
	public void mouseExited(MouseEvent _event) {
	}

	@Override
	public void mouseMoved(MouseEvent _event) {
		Point point = new Point(_event.getX(), window.invertScreenY(_event.getY()));

		if (window.actionItem != null) {
			if (hoveredBounds != null) {
				hoveredBounds.mouseExited(point);
			}
			window.actionItem.mouseMoved(point);
			return;
		}

		if (hoveredBounds != null) {
			if (hoveredBounds.contains(point)) {
				hoveredBounds.mouseMoved(point);
				return;

			} else {
				hoveredBounds.mouseExited(point);
				hoveredBounds = null;
			}
		}

		if (focusedBounds != null) {
			if (focusedBounds.contains(point)) {
				focusedBounds.mouseEntered(point);
				hoveredBounds = focusedBounds;
				return;
			}
		}

		ArrayList<Bounds> visibleBounds = window.getVisibleBounds();
		for (Type type : ScoreItem.TYPE_LIST) {
			for (Bounds bounds : visibleBounds) {

				if (bounds.scoreItem.type == type && bounds.contains(point)) {
					bounds.mouseEntered(point);
					hoveredBounds = bounds;
					return;
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent _event) {
		Point point = new Point(_event.getX(), window.invertScreenY(_event.getY()));

//		LEFT Mouse Button
		if (_event.getButton() == MouseEvent.BUTTON1) {
			mouseLeftIsPressed = true;

			if (window.actionItem != null) {
				window.actionItem.mousePressed(point);

				window.repaint();
				return;
			}

			if (focusedBounds != null && focusedBounds.contains(point)) {
				ActionItem returnAction = focusedBounds.mousePressed(point);
				if (returnAction != null) {
					window.setActionItem(returnAction);
				}

				window.repaint();
				return;
			}

			ArrayList<Bounds> visibleBounds = window.getVisibleBounds();
			for (int i = (ScoreItem.TYPE_LIST.size() - 1); i >= 0; i--) {
				Type type = ScoreItem.TYPE_LIST.get(i);

				for (Bounds bounds : visibleBounds) {
					if (bounds.scoreItem.type == type && bounds.contains(point)) {

						if (focusedBounds != null) {
							focusedBounds.setHasFocus(false);
						}
						focusedBounds = bounds;
						bounds.setHasFocus(true);

						ActionItem returnAction = bounds.mousePressed(point);
						if (returnAction != null) {
							window.setActionItem(returnAction);
						}

						window.repaint();
						return;
					}
				}

			}

			if (KeyManager.has(KeyEvent.VK_SHIFT)) {
				L.l("WindowListener.mousePressed()", "LEFT mouse button && VK_SHIFT");
				window.setActionItem(new MultiSelect(window, point));
				window.repaint();

			} else {
				isMoving = true;
				prevMovePoint = point;
			}

//		RIGHT Mouse Button
		} else if (_event.getButton() == MouseEvent.BUTTON3) {
			mouseRightIsPressed = true;

			if (isMoving) {
				isMoving = false;
				return;

			} else {
				ArrayList<Bounds> visibleBounds = window.getVisibleBounds();
				for (ScoreItem.Type type : ScoreItem.TYPE_LIST) {
					for (Bounds bounds : visibleBounds) {

						if (bounds.scoreItem.type == type && bounds.contains(point)) {
							focusedBounds = bounds;
							L.l("WindowListener.mousePressed()", "bounds.createPopupMenu()");
							bounds.createPopupMenu(_event);

							window.repaint();
							return;
						}
					}
				}

			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent _event) {
		Point point = new Point(_event.getX(), window.invertScreenY(_event.getY()));

//		LEFT Mouse Button
		if (_event.getButton() == MouseEvent.BUTTON1) {
			mouseLeftIsPressed = false;

			if (isMoving) {
				int dX = (point.x - prevMovePoint.x);
				int dY = (point.y - prevMovePoint.y);
				window.moveContents(dX, dY);
				isMoving = false;

			} else {
				if (window.actionItem != null) {
					window.actionItem.mouseReleased(point);
				} else if (focusedBounds != null) {
					focusedBounds.mouseReleased(point);
				}
			}

			window.repaint();

		} else if (_event.getButton() == MouseEvent.BUTTON3) {
			mouseRightIsPressed = false;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent _event) {

		if (!mouseLeftIsPressed && window.actionItem == null) {
			float wheelAmount = (float) _event.getPreciseWheelRotation();
			mouseWheel = (mouseWheel + wheelAmount);

			if (mouseWheel >= 1 || mouseWheel <= -1) {
				Point mouseLocation = new Point(_event.getX(), window.invertScreenY(_event.getY()));

				if (KeyManager.has(KeyEvent.VK_ALT) && KeyManager.has(KeyEvent.VK_CONTROL)) {
					if (mouseWheel > 0) {
						window.xZoom(H_ZOOM_OUT_PER_SCROLL, mouseLocation.x);
						window.repaint();
					} else {
						window.xZoom(H_ZOOM_IN_PER_SCROLL, mouseLocation.x);
						window.repaint();
					}

				} else if (KeyManager.has(KeyEvent.VK_ALT)) {
					if (mouseWheel > 0) {
						window.yZoom(V_ZOOM_OUT_PER_SCROLL, mouseLocation.y);
						window.repaint();
					} else {
						window.yZoom(V_ZOOM_IN_PER_SCROLL, mouseLocation.y);
						window.repaint();
					}

				} else if (KeyManager.has(KeyEvent.VK_CONTROL)) {
					if (mouseWheel > 0) {
						window.moveContents(MOVE_PER_SCROLL, 0);
						window.repaint();
					} else {
						window.moveContents(-MOVE_PER_SCROLL, 0);
						window.repaint();
					}

				} else {
					if (mouseWheel > 0) {
						window.moveContents(0, MOVE_PER_SCROLL);
						window.repaint();
					} else {
						window.moveContents(0, -MOVE_PER_SCROLL);
						window.repaint();
					}
				}
			}

			if (mouseWheel > 0) {
				mouseWheel = mouseWheel - 1;
			} else {
				mouseWheel = mouseWheel + 1;
			}
		}
	}

	void removeFocus() {
		focusedBounds.setHasFocus(false);
		focusedBounds = null;
	}

}
