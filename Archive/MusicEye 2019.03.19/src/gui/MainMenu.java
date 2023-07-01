package gui;

import java.nio.file.Path;

import javax.swing.JMenuBar;

import edit.EditMenu;
import file.FileMenu;
import project.Project;
import sound.SoundMenu;

@SuppressWarnings("serial")
public class MainMenu extends JMenuBar {


	public FileMenu		fileMenu;
	public EditMenu		editMenu;
	public SoundMenu	soundMenu;
	public ViewMenu		viewMenu;


	public MainMenu(Project _project, Path _path) {
		super();

		fileMenu = new FileMenu(_project, _path);
		add(fileMenu);
		editMenu = new EditMenu(_project);
		add(editMenu);
		soundMenu = new SoundMenu(_project);
		add(soundMenu);
		viewMenu = new ViewMenu(_project);
		add(viewMenu);
	}

}
