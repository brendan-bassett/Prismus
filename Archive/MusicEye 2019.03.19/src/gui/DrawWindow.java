package gui;

import java.awt.Dimension;

import javax.swing.JComponent;

import project.Project;

public class DrawWindow extends JComponent {

	private static final long	serialVersionUID	= -2257789039175697724L;


	public final Rect			drawRect			= new Rect();
	public final Project		project;


	public DrawWindow(Project _project) {
		project = _project;

		this.setPreferredSize(new Dimension(drawRect.width, drawRect.height));
	}

	public DrawWindow(Project _project, Rect _preferredSizeRect) {
		project = _project;
		drawRect.set(_preferredSizeRect);

		this.setPreferredSize(new Dimension(drawRect.width, drawRect.height));
	}

}
