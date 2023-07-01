package edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import project.Project;
import util.L;

@SuppressWarnings("serial")
public class EditMenu extends JMenu implements ActionListener {

	static private final String	TITLE			= "Edit";


	public ArrayList<Undoable>	undoList		= new ArrayList<Undoable>();
	public ArrayList<Undoable>	redoList		= new ArrayList<Undoable>();

	private Project				project;

	private JMenuItem			undoItem		= new JMenuItem("Undo", KeyEvent.VK_U);
	private JMenuItem			redoItem		= new JMenuItem("Redo", KeyEvent.VK_R);
	private JMenuItem			beatSnapItem	= new JCheckBoxMenuItem("Enable Beat Snapping");


	public EditMenu(Project _project) {
		super(TITLE);
		project = _project;

		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undoItem.addActionListener(this);
		undoItem.setEnabled(false);
		this.add(undoItem);

		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redoItem.addActionListener(this);
		redoItem.setEnabled(false);
		this.add(redoItem);

		this.addSeparator();

		beatSnapItem.setSelected(true);
		this.add(beatSnapItem);
	}


	@Override
	public void actionPerformed(ActionEvent _event) {
		Object source = _event.getSource();

		if (source == undoItem) {
			if (undoList.size() != 0) {
				Undoable undoable = undoList.remove(0);
				undoable.undo();
				if (undoList.size() == 0) {
					undoItem.setEnabled(false);
				}

				redoList.add(0, undoable);
				if (!redoItem.isEnabled()) {
					redoItem.setEnabled(true);
				}

			} else {
				L.e("EditMenu.actionPerformed()", "undoList.size() == 0");
			}

		} else if (source == redoItem) {
			if (redoList.size() != 0) {
				Undoable undoable = redoList.remove(0);
				undoable.redo();
				if (redoList.size() == 0) {
					redoItem.setEnabled(false);
				}

				undoList.add(0, undoable);
				if (!undoItem.isEnabled()) {
					undoItem.setEnabled(true);
				}

			} else {
				L.e("EditMenu.actionPerformed()", "redoList.size() == 0");
			}
		}

		project.repaint();
	}

	public void addUndoable(Undoable _undoable) {
		if (undoList.size() == 0) {
			undoItem.setEnabled(true);
		}
		undoList.add(0, _undoable);

		if (redoList.size() != 0) {
			redoList.clear();
			redoItem.setEnabled(false);
		}
	}

	public boolean isBeatSnapEnabled() {
		return beatSnapItem.isSelected();
	}

}
