package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import note.Note;
import project.Bounds;
import project.ScoreItem;
import project.ScoreItem.Type;

public class IntervalWindow extends DrawWindow implements STYLE.INTERVAL_WINDOW {

	private static final long		serialVersionUID	= -7864263072015005830L;


	private final ScoreWindow		window;
	public final JFrame				enclosingFrame;
	private Note					rootNote			= null;
	private final ArrayList<Note>	currentNoteList		= new ArrayList<Note>();


	public IntervalWindow(ScoreWindow _window, JFrame _frame) {
		super(_window.project, new Rect(0, DEFAULT_HEIGHT, DEFAULT_WIDTH, 0));
		window = _window;
		enclosingFrame = _frame;

		Bounds focusedBounds = _window.getFocusedBounds();
		if (focusedBounds != null && focusedBounds.scoreItem.type == Type.NOTE) {
			updateNote((Note) focusedBounds.scoreItem);
		}
	}


	@Override
	public void paintComponent(Graphics _graphics) {
		super.paintComponent(_graphics);
		CustomGraphics cg = new CustomGraphics(this, (Graphics2D) _graphics, drawRect);

		String rootStr = rootNote.parentRatio.toString();
		cg.text(rootStr,
				this.drawRect.centerX(),
				(this.drawRect.bottom - 20),
				CustomGraphics.Align.CENTER_BOTTOM,
				ROOT_FONT,
				ROOT_TEXT_COLOR);
	}

	public void updateNote(Note _note) {
		if (rootNote != _note) {
			rootNote = _note;

			currentNoteList.clear();
			for (Note n : project.noteList) {
				if (n.intersects(rootNote)) {
					currentNoteList.add(n);
				}
			}

			this.repaint();
		}
	}

}
