package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import util.CustomException;
import util.L;
import file.FileElement;
import file.FileMenu;
import project.Project;

@SuppressWarnings("serial")
public class WelcomeFrame extends JFrame implements ActionListener {

	static private final String	FRAME_NAME		= "Music Eye";
	static private final String	OPEN			= "OPEN existing project";
	static private final String	NEW				= "Start NEW project";
	static private final int	START_WIDTH		= 350;
	static private final int	START_HEIGHT	= 150;

	private JButton				openButton;
	private JButton				newButton;


	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				new WelcomeFrame();
			}
		});
	}

	public WelcomeFrame() {
		super(FRAME_NAME);

		setPreferredSize(new Dimension(START_WIDTH, START_HEIGHT));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		newButton = new JButton(NEW);
		newButton.setMnemonic(KeyEvent.VK_N);
		newButton.addActionListener(this);
		add(newButton, BorderLayout.LINE_END);

		openButton = new JButton(OPEN);
		openButton.setMnemonic(KeyEvent.VK_O);
		openButton.addActionListener(this);
		add(openButton, BorderLayout.LINE_START);

		pack();
		setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent _event) {
		Object source = _event.getSource();

		if (source == openButton) {
			JFileChooser fileChooser = new JFileChooser();
			File defaultDirectory = new File(FileMenu.DEFAULT_DIRECTORY);

			FileFilter filter = new FileNameExtensionFilter("XML file", FileMenu.FILE_TYPE_EXTENSION);
			fileChooser.setFileFilter(filter);
			fileChooser.setCurrentDirectory(defaultDirectory);
			int returnVal = fileChooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				L.l("FileMenu.open()", "file name == " + file.getName());

				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = dbf.newDocumentBuilder();
					Document document = builder.parse(file);

					FileElement projectE = new FileElement(document.getDocumentElement(), document);
					Project.read(file.toPath(), projectE);

					dispose();

				} catch (ParserConfigurationException | SAXException | IOException _e) {
					_e.printStackTrace();
					L.e("could not build or parse document");
				} catch (CustomException _e) {
				}
			}

		} else if (source == newButton) {
			new Project(FRAME_NAME);
			dispose();
		}
	}

}
