package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;


public class WindowPanel extends JPanel implements STYLE.SCORE_WINDOW {

	private static final long	serialVersionUID	= 1L;


	public WindowPanel(ScoreWindow _window) {
		
//		TODO: create menu which applies only to window
		
		this.setLayout(new BorderLayout());

		this.add(_window, BorderLayout.CENTER);
		this.add(_window.hNavBar, BorderLayout.NORTH);
		this.add(_window.vNavBar, BorderLayout.EAST);
	}

}
