package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import project.Project;

@SuppressWarnings("serial")
public class ViewMenu extends JMenu implements ActionListener {

	static private final String		NAME					= "View";


	private final Project			project;

	private final JMenuItem			intervalInspectorItem	= new JMenuItem("Interval Inspector");
	private final JCheckBoxMenuItem	overtoneItem			= new JCheckBoxMenuItem("Show Overtones");
	public boolean					showOvertones			= false;


	public ViewMenu(Project _project) {
		super(NAME);
		project = _project;

		intervalInspectorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.SHIFT_MASK));
		intervalInspectorItem.addActionListener(this);
		this.add(intervalInspectorItem);

		overtoneItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, ActionEvent.CTRL_MASK));
		overtoneItem.addActionListener(this);
		this.add(overtoneItem);
	}


	@Override
	public void actionPerformed(ActionEvent _event) {
		Object source = _event.getSource();

		if (source == overtoneItem) {
			showOvertones = overtoneItem.isSelected();
			project.resizeAllBounds(false);
			project.repaint();

		} else if (source == intervalInspectorItem) {
			project.intervalWindow.enclosingFrame.setVisible(true);
		}
	}

}
