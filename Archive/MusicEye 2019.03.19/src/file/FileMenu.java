package file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import project.Project;
import util.CustomException;
import util.L;

@SuppressWarnings("serial")
public class FileMenu extends JMenu implements ActionListener {

	static public final String	DEFAULT_DIRECTORY	= "H:/MusicEye Saves";
	static private final String	FILE_INIT			= "<?xml version=\"1.0\"?> <PROJECT> </PROJECT>";
	static public final String	FILE_TYPE_EXTENSION	= "xml";
	static private final String	NAME				= "File";


	private Project				project;

	private JMenuItem			saveItem			= new JMenuItem("Save", KeyEvent.VK_S);
	private JMenuItem			saveAsItem			= new JMenuItem("Save As...");
	private JMenuItem			openItem			= new JMenuItem("Open", KeyEvent.VK_O);

	private JFileChooser		fileChooser;
	private File				defaultDirectory;
	private Path				path;


	public FileMenu(Project _project, Path _path) {
		super(NAME);
		project = _project;
		path = _path;

		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(this);
		saveItem.setEnabled(false);
		add(saveItem);

		saveAsItem.addActionListener(this);
		add(saveAsItem);

		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openItem.addActionListener(this);
		add(openItem);

		fileChooser = new JFileChooser();
		defaultDirectory = new File(DEFAULT_DIRECTORY);
		fileChooser.setCurrentDirectory(defaultDirectory);
	}


	@Override
	public void actionPerformed(ActionEvent _event) {
		Object source = _event.getSource();

		if (source == saveItem) {
			save();
		} else if (source == saveAsItem) {
			saveAs();
		} else if (source == openItem) {
			open();
		}
	}

	public void open() {
		FileFilter filter = new FileNameExtensionFilter("XML file", FILE_TYPE_EXTENSION);
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(project);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			L.l("FileMenu.open", "file name == " + file.getName());

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document document = builder.parse(file);

				FileElement projectE = new FileElement(document.getDocumentElement(), document);
				Project.read(file.toPath(), projectE);

				project.dispose();

			} catch (SAXException | IOException | ParserConfigurationException | CustomException e) {
				L.e("could not read file");
				e.printStackTrace();
			}
		}
	}

	private void save() {
		if (path != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				StringBuilder sb = new StringBuilder();
				sb.append(FILE_INIT);
				ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
				Document document = builder.parse(stream);

				project.write(new FileElement(document.getDocumentElement(), document));

				File file = path.toFile();
				file.delete();
				file.createNewFile();

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(file);
				transformer.transform(source, result);

			} catch (SAXException | IOException | TransformerException | ParserConfigurationException | CustomException e) {
				L.e("could not write file");
				e.printStackTrace();
			}
			L.l("FileMenu.save", "path == " + path);

		} else {
			L.e("FileMenu.save", "path == null");
		}
	}

	private void saveAs() {
		FileFilter filter = new FileNameExtensionFilter("XML file", FILE_TYPE_EXTENSION);
		fileChooser.setFileFilter(filter);
		fileChooser.setName("Save As...");

		int returnVal = fileChooser.showSaveDialog(project);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fileChooser.getSelectedFile();

			try {
				String filePath = file.getAbsolutePath();
				if (!filePath.toLowerCase().endsWith("." + FILE_TYPE_EXTENSION)) {
					file = new File(filePath + "." + FILE_TYPE_EXTENSION);
				}

				if (file.exists()) {
					int confirmation = JOptionPane.showConfirmDialog(	fileChooser,
																		"A file with this name already exists. Overwrite it?",
																		"Save As...",
																		JOptionPane.YES_NO_OPTION,
																		JOptionPane.QUESTION_MESSAGE);
					if (confirmation == JOptionPane.YES_OPTION) {
						file.delete();
					} else {
						return;
					}
				}
				file.createNewFile();

				L.l("FileMenu.saveAs", "FILE.PROJECT_E.FILE_INIT==" + FILE_INIT);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				StringBuilder sb = new StringBuilder();
				sb.append(FILE_INIT);
				ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
				Document document = builder.parse(stream);

				project.write(new FileElement(document.getDocumentElement(), document));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(file);
				transformer.transform(source, result);

			} catch (SAXException | IOException | TransformerException | ParserConfigurationException | CustomException e) {
				L.e("could not write file");
				e.printStackTrace();
			}

			path = file.toPath();
			saveItem.setEnabled(true);
			L.l("FileMenu.saveAs", "file.getName() == " + file.getName());
		}
	}

}
