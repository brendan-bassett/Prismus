package note;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import gui.ScoreWindow;
import project.Part;
import project.Project;


public class NotePopupMenu extends JPopupMenu {

	private static final long				serialVersionUID	= -8685466309312962218L;

	private final ScoreWindow					window;
	private final Note						selectedNote;
	private final Project					project;

	private final OvertoneMenuItem			overtoneItem		= new OvertoneMenuItem("Show Overtones");

	private final JMenu						partMenu			= new JMenu("Change Part");
	private final ButtonGroup				partButtonGroup		= new ButtonGroup();


	NotePopupMenu(ScoreWindow _window, Note _note) {
		window = _window;
		selectedNote = _note;
		project = selectedNote.project;

		boolean showOvertones = false;
		if (selectedNote.overtones != null) {
			showOvertones = true;
		}

		overtoneItem.setSelected(showOvertones);
		overtoneItem.setMnemonic(KeyEvent.VK_O);
		add(overtoneItem);

		add(partMenu);
		for (Part part : selectedNote.project.partList) {
			PartMenuItem partMenuItem = new PartMenuItem(part);
			partButtonGroup.add(partMenuItem);
			partMenu.add(partMenuItem);
		}

	}


	private class OvertoneMenuItem extends JCheckBoxMenuItem implements ActionListener {

		private static final long serialVersionUID = 1L;


		public OvertoneMenuItem(String _text) {
			super(_text);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent _event) {
			selectedNote.showOvertones(overtoneItem.isSelected());
		}
	}

	private class PartMenuItem extends JRadioButtonMenuItem implements ActionListener {

		private static final long	serialVersionUID	= 1L;
		final Part					part;


		PartMenuItem(Part _part) {
			super(_part.name);
			part = _part;

			addActionListener(this);
			
			if(part == selectedNote.part) {
				this.setSelected(true);
			}

			for (Note note : project.noteList) {
				if (note.part == part && note.intersects(window.visibleRange.leftT, window.visibleRange.rightT)) {
					if(selectedNote.intersects(note)) {
						this.setEnabled(false);
					}
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent _event) {
			selectedNote.setPart(part);
		}
	}

}
