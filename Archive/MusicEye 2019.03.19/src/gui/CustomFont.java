package gui;

import gui.CustomGraphics.Align;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.util.ArrayList;


public class CustomFont extends Font {

	private static final long				serialVersionUID	= 4857923411705655897L;

	private final ArrayList<FontMetrics>	fontMetricsList		= new ArrayList<FontMetrics>();
	private final ArrayList<ScoreWindow>	windowList			= new ArrayList<ScoreWindow>();


	public CustomFont(String _name, int _style, int _size) {
		super(_name, _style, _size);
	}


	public Rect getEnclosingRect(	ScoreWindow _window,
									String _string,
									Insets _inset,
									int _x,
									int _y,
									Align _align,
									boolean _includeDescent) {
		FontMetrics fontMetrics = getFontMetrics(_window);

		int width = fontMetrics.stringWidth(_string);
		int l = 0;
		int r = 0;
		switch (_align.hAlign) {
			case CENTER:
				l = _x - (width / 2);
				r = l + width;
			break;

			case LEFT:
				l = _x;
				r = l + width;
			break;

			case RIGHT:
				r = _x;
				l = r - width;
		}

		int height;
		if (_includeDescent) {
			height = fontMetrics.getAscent() + fontMetrics.getDescent();
		} else {
			height = fontMetrics.getAscent();
		}

		int b = 0;
		int t = 0;
		switch (_align.vAlign) {
			case BOTTOM:
				b = _y;
				t = b + height;
			break;

			case CENTER:
				b = _y - (height / 2);
				t = b + height;
			break;

			case TOP:
				t = _y;
				b = t - height;
		}

		l = l - _inset.left;
		t = t + _inset.top;
		r = r + _inset.right;
		b = b - _inset.bottom;

		return new Rect(l, t, r, b);
	}

	public FontMetrics getFontMetrics(DrawWindow _window) {
		for (int i = 0; i < windowList.size(); i++) {
			ScoreWindow window = windowList.get(i);
			if (window == _window) {
				return fontMetricsList.get(i);
			}
		}

		FontMetrics fontMetrics = _window.getFontMetrics(this);
		fontMetricsList.add(fontMetrics);
		return fontMetrics;
	}

}
