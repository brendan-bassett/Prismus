package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

@SuppressWarnings("serial")
public class NavBar extends JComponent implements STYLE.GLOBAL, ComponentListener, MouseInputListener {

	private static final int	DEFAULT_SIZE		= 15;
	private static final float	H_ZOOM_IN_RATIO		= 1.33f;
	private static final float	H_ZOOM_OUT_RATIO	= 0.67f;
	private static final float	V_ZOOM_IN_RATIO		= 1.33f;
	private static final float	V_ZOOM_OUT_RATIO	= 0.67f;
	private static final int	MOVE_NULL			= -100;
	private static final Color	FILL_COLOR			= Color.LIGHT_GRAY;
	private static final Color	BORDER_COLOR		= Color.BLACK;


	static public enum Orientation {
		HOZ,
		VERT
	}


	public final Rect			rect		= new Rect();
	public final Rect			moveRect	= new Rect();
	public final Rect			windowRect	= new Rect();
	public final Rect			zoomInRect	= new Rect();
	public final Rect			zoomOutRect	= new Rect();

	private final ScoreWindow	window;
	private final Orientation	orientation;
	private float				moveTime	= MOVE_NULL;
	private float				moveRelP	= MOVE_NULL;


	public NavBar(ScoreWindow _window, Orientation _orientation) {
		super();

		window = _window;
		orientation = _orientation;

		Dimension size;
		if (orientation == Orientation.HOZ) {
			size = new Dimension(window.drawRect.width + DEFAULT_SIZE, DEFAULT_SIZE);
		} else { // VERT
			size = new Dimension(DEFAULT_SIZE, window.drawRect.height);
		}

		this.setPreferredSize(size);
		setSize(size);

		setFocusable(true);
		setOpaque(true);

		addMouseListener(this);
		addMouseMotionListener(this);

		resize();
	}


	@Override
	public void componentHidden(ComponentEvent _event) {
	}

	@Override
	public void componentMoved(ComponentEvent _event) {
	}

	@Override
	public void componentResized(ComponentEvent _event) {
		rect.set(0, getHeight(), getWidth(), 0);
	}

	@Override
	public void componentShown(ComponentEvent _event) {
	}

	@Override
	public void mouseClicked(MouseEvent _event) {
		Point point;
		switch (orientation) {
			case HOZ:
				point = _event.getPoint();
			break;
			default: //VERT
				point = new Point(_event.getX(), window.invertScreenY(_event.getY()));
		}

		if (!windowRect.contains(point)) {
			if (zoomInRect.contains(point)) {
				switch (orientation) {
					case HOZ:
						window.xZoom(H_ZOOM_IN_RATIO);
					break;
					case VERT:
						window.yZoom(V_ZOOM_IN_RATIO);
				}

			} else if (zoomOutRect.contains(point)) {
				switch (orientation) {
					case HOZ:
						window.xZoom(H_ZOOM_OUT_RATIO);
					break;
					case VERT:
						window.yZoom(V_ZOOM_OUT_RATIO);
				}

			} else {
				switch (orientation) {
					case HOZ:
						window.centerOnTime(timeFromBarLoc(point));
					break;
					case VERT:
						window.centerOnRelP(relPFromBarLoc(point));
				}
			}

			window.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent _event) {
		Point point;
		switch (orientation) {
			case HOZ:
				point = _event.getPoint();
			break;
			default: //VERT
				point = new Point(_event.getX(), window.invertScreenY(_event.getY()));
		}

		switch (orientation) {
			case HOZ: //HOZ
				if (moveTime != MOVE_NULL) {
					float mouseT = timeFromBarLoc(point);
					float dT = (mouseT - moveTime);
					int dH = Math.round(dT * window.pps);
					window.moveContents(-dH, 0);
					moveTime = timeFromBarLoc(point);
				}
			break;

			default: //VERT
				if (moveRelP != MOVE_NULL) {
					float mouseRelP = relPFromBarLoc(point);
					float dRelP = (mouseRelP - moveRelP);
					int dV = Math.round(dRelP * window.ppo);
					window.moveContents(0, -dV);
					moveRelP = relPFromBarLoc(point);
				}
		}

		window.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent _event) {
		Point point;
		switch (orientation) {
			case HOZ:
				point = _event.getPoint();
			break;
			default: //VERT
				point = new Point(_event.getX(), window.invertScreenY(_event.getY()));
		}

		if (windowRect.contains(point)) {
			switch (orientation) {
				case HOZ: //HOZ
					moveTime = timeFromBarLoc(point);
				break;
				default: //VERT
					moveRelP = relPFromBarLoc(point);
			}
		}

		window.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent _event) {
		Point point;
		switch (orientation) {
			case HOZ:
				point = _event.getPoint();
			break;
			default: //VERT
				point = new Point(_event.getX(), window.invertScreenY(_event.getY()));
		}

		switch (orientation) {
			case HOZ: //HOZ
				if (moveTime != MOVE_NULL) {
					float mouseT = timeFromBarLoc(point);
					float dT = (mouseT - moveTime);
					int dH = Math.round(dT * window.pps);
					window.moveContents(dH, 0);
					moveTime = MOVE_NULL;
				}
			break;

			default: //VERT
				if (moveRelP != MOVE_NULL) {
					float mouseRelP = relPFromBarLoc(point);
					float dRelP = (mouseRelP - moveRelP);
					int dV = Math.round(dRelP * window.ppo);
					window.moveContents(0, -dV);
					moveRelP = MOVE_NULL;
				}
		}

		window.repaint();
	}

	@Override
	public void paintComponent(Graphics _graphics) {
		super.paintComponent(_graphics);
		CustomGraphics g = new CustomGraphics(window, (Graphics2D) _graphics, rect);

		g.fillRect(windowRect, FILL_COLOR);
		g.drawRect(windowRect, BORDER_COLOR);

		g.drawRect(zoomInRect, BORDER_COLOR);
		g.line((zoomInRect.left + 2), zoomInRect.centerY(), (zoomInRect.right - 2), zoomInRect.centerY(), BORDER_COLOR);
		g.line(zoomInRect.centerX(), (zoomInRect.top - 3), zoomInRect.centerX(), (zoomInRect.bottom + 1), BORDER_COLOR);

		g.drawRect(zoomOutRect, BORDER_COLOR);
		g.line((zoomOutRect.left + 3), zoomOutRect.centerY(), (zoomOutRect.right - 3), zoomOutRect.centerY(), BORDER_COLOR);

		g.drawRect(rect, BORDER_COLOR, STROKE.BASIC_2);
	}

	private float relPFromBarLoc(Point _point) {
		int pTop = window.topLimit();
		int pBottom = window.bottomLimit();
		int pHeight = (pTop - pBottom);
		int mHeight = moveRect.height;
		int mDifY = (_point.y - moveRect.bottom);

		float pRatio = ((float) pHeight / (float) mHeight);
		float pDifY = (mDifY * pRatio);
		float pY = (pBottom + pDifY);
		float relP = (pY / window.ppo);

		return relP;
	}

	public void resize() {
		int left = 0;
		int top = getHeight();
		int right = getWidth();
		int bottom = 0;
		rect.set(left, top, right, bottom);

		switch (orientation) {
			case HOZ:
				moveRect.set(left, top, (right - (DEFAULT_SIZE * 2)), bottom);

				int pRight = window.rightLimit();
				int pLeft = window.leftLimit();
				int pWidth = (pRight - pLeft);

				float hScale = ((float) moveRect.width / (float) pWidth);

				int leftDif = (pRight - window.graphFrame.left);
				int rightDif = (pRight - window.graphFrame.right);
				int wLeft = (moveRect.right - Math.round(leftDif * hScale));
				int wRight = (moveRect.right - Math.round(rightDif * hScale));
				windowRect.set(wLeft, top, wRight, bottom);

				zoomInRect.set((right - DEFAULT_SIZE), top, right, bottom);
				zoomOutRect.set((zoomInRect.left - DEFAULT_SIZE), top, zoomInRect.left, bottom);
			break;

			case VERT:
				moveRect.set(left, top, right, (bottom + (DEFAULT_SIZE * 2)));

				int pTop = window.topLimit();
				int pBottom = window.bottomLimit();
				int pHeight = (pTop - pBottom);

				float vScale = ((float) moveRect.height / (float) pHeight);

				int bottomDif = (pTop - window.graphFrame.bottom);
				int topDif = (pTop - window.graphFrame.top);
				int wTop = (top - Math.round(topDif * vScale));
				int wBottom = (top - Math.round(bottomDif * vScale));
				windowRect.set(left, wTop, right, wBottom);

				zoomOutRect.set(left, (bottom + DEFAULT_SIZE), right, bottom);
				zoomInRect.set(left, (zoomOutRect.top + DEFAULT_SIZE), right, zoomOutRect.top);
		}
	}

	private float timeFromBarLoc(Point _point) {
		int pRight = window.rightLimit();
		int pLeft = window.leftLimit();
		int pWidth = (pRight - pLeft);
		int mWidth = moveRect.width;
		int mDifX = (_point.x - moveRect.left);

		float pRatio = ((float) pWidth / (float) mWidth);
		float pDifX = (mDifX * pRatio);
		float pX = (pLeft + pDifX);
		float time = (pX / window.pps);

		return time;
	}

}
