package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import util.L;

public class CustomGraphics implements STYLE.GLOBAL {

	static enum HAlign {
		LEFT,
		CENTER,
		RIGHT
	}

	static enum VAlign {
		BOTTOM,
		CENTER,
		TOP
	}

	static public enum Align {
		LEFT_BOTTOM("LEFT_BOTTOM", HAlign.LEFT, VAlign.BOTTOM),
		LEFT_CENTER("LEFT_CENTER", HAlign.LEFT, VAlign.CENTER),
		LEFT_TOP("LEFT_TOP", HAlign.LEFT, VAlign.TOP),
		CENTER_BOTTOM("CENTER_BOTTOM", HAlign.CENTER, VAlign.BOTTOM),
		CENTER_CENTER("CENTER_CENTER", HAlign.CENTER, VAlign.CENTER),
		CENTER_TOP("CENTER_TOP", HAlign.CENTER, VAlign.TOP),
		RIGHT_BOTTOM("RIGHT_BOTTOM", HAlign.RIGHT, VAlign.BOTTOM),
		RIGHT_CENTER("RIGHT_CENTER", HAlign.RIGHT, VAlign.CENTER),
		RIGHT_TOP("RIGHT_TOP", HAlign.RIGHT, VAlign.TOP);

		final private String	string;
		final HAlign			hAlign;
		final VAlign			vAlign;


		Align(String _string, HAlign _hAlign, VAlign _vAlign) {
			string = _string;
			hAlign = _hAlign;
			vAlign = _vAlign;
		}

		public String toString() {
			return string;
		}
	}


	private final DrawWindow	window;
	private final Graphics2D	graphics;
	private final boolean		invertY;

	private Color				activeColor		= Color.BLACK;
	private BasicStroke			activeStroke	= STROKE.BASIC_1;


	public CustomGraphics(ScoreWindow _scoreWindow, Graphics2D _g, Rect _rect) {
		window = _scoreWindow;
		graphics = _g;
		invertY = true;

//		Implement drawing w/ alpha values
		_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		_g.setPaint(Color.WHITE);
		_g.fillRect(0, 0, _rect.width, _rect.height);

		_g.scale(1, -1);
		_g.translate(0, -_rect.height);

		_g.setPaint(activeColor);
		_g.setStroke(activeStroke);
	}

	public CustomGraphics(DrawWindow _window, Graphics2D _g, Rect _rect) {
		window = _window;
		graphics = _g;
		invertY = false;

//		Implement drawing w/ alpha values
		_g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		_g.setPaint(Color.WHITE);
		_g.fillRect(0, 0, _rect.width, _rect.height);
		L.l("CustomGraphics", "rect==" + _rect.toString());

		_g.setPaint(activeColor);
		_g.setStroke(activeStroke);
	}


	public void drawCircle(int _centerX, int _centerY, int _radius, Color _color) {
		drawCircle(_centerX, _centerY, _radius, _color, STROKE.BASIC_1);
	}

	public void drawCircle(int _centerX, int _centerY, int _radius, Color _color, BasicStroke _stroke) {
		set(_color, _stroke);
		graphics.drawOval((_centerX - _radius), (_centerY - _radius), (_radius * 2), (_radius * 2));
	}

	public void drawOval(int _left, int _top, int _right, int _bottom, Color _color) {
		drawOval(_left, _top, _right, _bottom, _color, STROKE.BASIC_1);
	}

	public void drawOval(int _left, int _top, int _right, int _bottom, Color _color, BasicStroke _stroke) {
		set(_color, _stroke);
		graphics.drawOval(_left, _bottom, (_right - _left), (_top - _bottom));
	}

	public void drawOval(Rect _rect, Color _color) {
		drawOval(_rect, _color, STROKE.BASIC_1);
	}

	public void drawOval(Rect _rect, Color _color, BasicStroke _stroke) {
		drawOval(_rect.left, _rect.top, _rect.right, _rect.bottom, _color, _stroke);
	}

	public void drawPolygon(Polygon _poly, Color _color) {
		drawPolygon(_poly, _color, STROKE.BASIC_1);
	}

	public void drawPolygon(Polygon _poly, Color _color, BasicStroke _stroke) {
		set(_color, _stroke);
		graphics.drawPolygon(_poly);
	}

	public void drawRect(int _left, int _top, int _right, int _bottom, Color _color) {
		drawRect(_left, _top, _right, _bottom, _color, STROKE.BASIC_1);
	}

	public void drawRect(Rect _rect, Color _color) {
		drawRect(_rect.left, _rect.top, _rect.right, _rect.bottom, _color, STROKE.BASIC_1);
	}

	public void drawRect(Rect _rect, Color _color, BasicStroke _stroke) {
		drawRect(_rect.left, _rect.top, _rect.right, _rect.bottom, _color, _stroke);
	}

	public void drawRect(int _left, int _top, int _right, int _bottom, Color _color, BasicStroke _stroke) {
		set(_color, _stroke);
		graphics.drawRect(_left, _bottom, (_right - _left), (_top - _bottom));
	}


	public void fillCircle(int _centerX, int _centerY, int _radius, Color _color) {
		set(_color);
		graphics.fillOval((_centerX - _radius), (_centerY - _radius), (_radius * 2), (_radius * 2));
	}

	public void fillOval(int _left, int _top, int _right, int _bottom, Color _color) {
		set(_color);
		graphics.fillOval(_left, _bottom, (_right - _left), (_top - _bottom));
	}

	public void fillOval(Rect _rect, Color _color) {
		fillOval(_rect.left, _rect.top, _rect.right, _rect.bottom, _color);
	}

	public void fillPolygon(Polygon _poly, Color _color) {
		set(_color);
		graphics.fillPolygon(_poly);
	}

	public void fillRect(int _left, int _top, int _right, int _bottom, Color _color) {
		set(_color);
		graphics.fillRect(_left, _bottom, (_right - _left), (_top - _bottom));
	}

	public void fillRect(Rect _rect, Color _color) {
		fillRect(_rect.left, _rect.top, _rect.right, _rect.bottom, _color);
	}

	public void line(int _x1, int _y1, int _x2, int _y2, Color _color) {
		line(_x1, _y1, _x2, _y2, _color, STROKE.BASIC_1);
	}

	public void line(int _x1, int _y1, int _x2, int _y2, Color _color, BasicStroke _stroke) {
		set(_color, _stroke);
		graphics.drawLine(_x1, _y1, _x2, _y2);
	}


	private void set(Color _color) {
		if (activeColor != _color) {
			activeColor = _color;
			graphics.setPaint(_color);
		}
	}

	private void set(Color _color, BasicStroke _stroke) {
		if (activeColor != _color) {
			activeColor = _color;
			graphics.setPaint(_color);
		}

		if (activeStroke != _stroke) {
			activeStroke = _stroke;
			graphics.setStroke(_stroke);
		}
	}


	public void text(String _string, int _x, int _y, Align _align, CustomFont _font, Color _color) {
		set(_color);

		FontMetrics fontMetrics = _font.getFontMetrics(window);

		if (invertY) {
			graphics.scale(1, -1);
			graphics.translate(0, -window.drawRect.height);
		}

		int x = _x;
		switch (_align.hAlign) {
			case CENTER:
				x = (x - (fontMetrics.stringWidth(_string) / 2));
			break;

			case LEFT:
//				DO NOTHING
			break;

			case RIGHT:
				x = (x - fontMetrics.stringWidth(_string));
			break;
		}

		int y = _y;
		switch (_align.vAlign) {
			case CENTER:
				y = y + (((fontMetrics.getAscent() + fontMetrics.getDescent()) / 2) - fontMetrics.getAscent());
			break;

			case BOTTOM:
				y = y + fontMetrics.getDescent();
			break;

			case TOP:
				y = y - fontMetrics.getAscent();
			break;
		}

		if (invertY) {
			int invertedY = Math.round(window.drawRect.height - y);
			graphics.drawString(_string, x, invertedY);
			graphics.scale(1, -1);
			graphics.translate(0, -window.drawRect.height);
		}
	}

}
