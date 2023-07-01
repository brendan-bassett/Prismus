package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edit.Undoable;
import file.FILE;
import file.FileElement;
import file.FileMenu;
import gui.IntervalWindow;
import gui.KeyManager;
import gui.MainMenu;
import gui.PartManager;
import gui.STYLE;
import gui.ScoreWindow;
import gui.WindowPanel;
import note.Note;
import note.Overtones;
import util.CustomException;
import util.K;

/**
 * Created by Brendan on 4/1/2015.
 */
public class Project extends JFrame implements FILE.PROJECT_F, STYLE.PROJECT, WindowListener {

	private static final long serialVersionUID = -4934844247324833557L;


	static public void read(Path _path, FileElement _e) throws CustomException {
		String name = _e.get(NAME);
		float harmonicCenter = _e.get(HARMONIC_CENTER);
		int graphIDCounter = _e.get(GRAPH_ID_COUNTER);
		int intervalIDCounter = _e.get(INTERVAL_ID_COUNTER);
		int noteIDCounter = _e.get(NOTE_ID_COUNTER);

		FileElement defaultRatiosE = _e.getChild(DEFAULT_RATIOS_TAG);
		Iterator<FileElement> ratioEIterator = defaultRatiosE.getChildren(Ratio.TAG).iterator();
		ArrayList<Ratio> defaultRatios = new ArrayList<Ratio>();
		while (ratioEIterator.hasNext()) {
			defaultRatios.add(Ratio.read(ratioEIterator.next()));
		}

		Project project = new Project(name, defaultRatios, graphIDCounter, harmonicCenter, intervalIDCounter, noteIDCounter, _path);

		//		 --------------POPULATING SCORE: MUST REMAIN IN ORDER------------------
		for (FileElement graphE : _e.getChild(GRAPH_LIST_TAG).getChildren()) {
			try {
				project.graphList.add(Graph.read(project, graphE));
			} catch (CustomException _exception) {
			}
		}

		for (FileElement partE : _e.getChild(PART_LIST_TAG).getChildren()) {
			try {
				project.partList.add(Part.read(project, partE));
			} catch (CustomException _exception) {
			}
		}

		for (FileElement noteE : _e.getChild(NOTE_LIST_TAG).getChildren()) {
			try {
				project.noteList.add(Note.read(project, noteE));
			} catch (CustomException _exception) {
			}
		}


		for (FileElement tonicButtonE : _e.getChild(TONIC_BUTTON_LIST_TAG).getChildren()) {
			try {
				project.tonicButtonList.add(TonicButton.read(project, tonicButtonE));
			} catch (CustomException _exception) {
			}
		}

		for (FileElement timeMarkerE : _e.getChild(TIME_MARKER_LIST_TAG).getChildren()) {
			try {
				project.timeMarkerList.add(TimeMarker.read(project, timeMarkerE));
			} catch (CustomException _exception) {
			}
		}
		//		 --------------\POPULATING SCORE: MUST REMAIN IN ORDER\----------------

		FileElement windowListE = _e.getChild(WINDOW_LIST_TAG);
		ArrayList<FileElement> windowEList = windowListE.getChildren();
		for (int i = 0; i < windowEList.size(); i++) {
			project.addWindow(ScoreWindow.read(project, windowEList.get(i)));
		}

		project.pack();
		project.setVisible(true);
	}


	private int							graphIDCounter		= 0;							// int ranges from -2,147,483,648 to 2,147,483,647
	private int							intervalIDCounter	= 0;
	private int							noteIDCounter		= 0;

	public final ArrayList<Graph>		graphList			= new ArrayList<Graph>();
	public final ArrayList<Note>		noteList			= new ArrayList<Note>();
	public final ArrayList<Overtones>	overtonesList		= new ArrayList<Overtones>();
	public final ArrayList<Part>		partList			= new ArrayList<Part>();
	public final ArrayList<TimeMarker>	timeMarkerList		= new ArrayList<TimeMarker>();
	public final ArrayList<TonicButton>	tonicButtonList		= new ArrayList<TonicButton>();

	public final ArrayList<Ratio>		defaultRatios;
	public float						harmonicCenter;
	public String						name				= "";
	public ArrayList<ScoreWindow>		windowList			= new ArrayList<ScoreWindow>();
//	TODO: make windowList final?

	public final MainMenu				mainMenu;
	public final KeyManager				keyManager;
	public final IntervalWindow			intervalWindow;
	public final PartManager			partManager;
	public ScoreWindow					focusedWindow;

	public float						bottomRelP;
	public float						topRelP;


	public Project(String _name) {
		name = _name;

		defaultRatios = new ArrayList<Ratio>();
		defaultRatios.add(R11);
		defaultRatios.add(R98);
		defaultRatios.add(R76);
		defaultRatios.add(R65);
		defaultRatios.add(R54);
		defaultRatios.add(R43);
		defaultRatios.add(R75);
		defaultRatios.add(R32);
		defaultRatios.add(R85);
		defaultRatios.add(R53);
		defaultRatios.add(R74);
		defaultRatios.add(R95);

		harmonicCenter = DEFAULT_HARMONIC_CENTER;

		partList.add(new Part(new Color(255, 255, 102), 0, true, "Soprano")); // light yellow
		partList.add(new Part(new Color(255, 153, 102), 1, false, "Alto")); // light pink
		partList.add(new Part(new Color(153, 255, 255), 2, false, "Tenor")); // light blue
		partList.add(new Part(new Color(153, 255, 153), 3, false, "Bass")); // light green

		Graph graph = new Graph(this, 4, this.getNewGraphID(), 2, 16, 1, new Ratio(), 0, 60);
		graph.initAdd(); //TODO: figure out if graph.initAdd() is needed
		bottomRelP = K.relPFromHz(this, HZ_BOTTOM_LIMIT);
		topRelP = K.relPFromHz(this, HZ_TOP_LIMIT);

		mainMenu = new MainMenu(this, new File(FileMenu.DEFAULT_DIRECTORY).toPath());
		keyManager = new KeyManager(this);
		partManager = new PartManager(this);

		this.addWindow(new ScoreWindow(this));
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyManager);
		this.add(partManager);
		this.setJMenuBar(mainMenu);

		JFrame intervalFrame = new JFrame("Interval Inspector");
		intervalWindow = new IntervalWindow(this.focusedWindow, intervalFrame);
		intervalFrame.add(intervalWindow);
		intervalFrame.pack();
		intervalFrame.setVisible(false);

//		TODO:Line up partManager at top of page

		TimeMarker timeMarker = new TimeMarker(this, graph, 0);
		timeMarker.initAdd();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(this);
		this.pack();
		this.setVisible(true);
	}

	public Project(	String _name,
					ArrayList<Ratio> _defaultRatios,
					int _graphIDCounter,
					float _harmonicCenter,
					int _intervalIDCounter,
					int _noteIDCounter,
					Path _path) {
		super(_name);
		name = _name;
		defaultRatios = _defaultRatios;
		graphIDCounter = _graphIDCounter;
		harmonicCenter = _harmonicCenter;
		intervalIDCounter = _intervalIDCounter;
		noteIDCounter = _noteIDCounter;

		mainMenu = new MainMenu(this, _path);
		keyManager = new KeyManager(this);
		partManager = new PartManager(this);

		this.addWindow(new ScoreWindow(this));
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyManager);
		this.add(partManager);
		this.setJMenuBar(mainMenu);

		JFrame intervalFrame = new JFrame("Interval Inspector");
		intervalWindow = new IntervalWindow(this.focusedWindow, intervalFrame);
		intervalFrame.add(intervalWindow);
		intervalFrame.pack();
		intervalFrame.setVisible(false);

		bottomRelP = K.relPFromHz(this, HZ_BOTTOM_LIMIT);
		topRelP = K.relPFromHz(this, HZ_TOP_LIMIT);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(this);
		this.pack();
		this.setVisible(true);

	}


	public void add(ScoreItem _scoreItem) {
		switch (_scoreItem.type) {
			case GRAPH:
				graphList.add((Graph) _scoreItem);
			break;

			case NOTE:
				noteList.add((Note) _scoreItem);
			break;

			case OVERTONES:
				overtonesList.add((Overtones) _scoreItem);
			break;

			case TIME_MARKER:
				timeMarkerList.add((TimeMarker) _scoreItem);
			break;

			case TONIC_BUTTON:
				tonicButtonList.add((TonicButton) _scoreItem);
		}
	}

	public void addUndoable(Undoable _undoable) {
		mainMenu.editMenu.addUndoable(_undoable);
	}

	public void addWindow(ScoreWindow _window) {
		windowList.add(_window);
		focusedWindow = _window;

		if (windowList.size() == 1) {
			getContentPane().add(new WindowPanel(_window), BorderLayout.WEST);
			Dimension size = getLayout().preferredLayoutSize(this);
			setSize(size);
			repaint();
		}
	}

	public float getEndTime() {
		float endTime = 0;

		for (Graph g : graphList) {
			if (g.endTime > endTime) {
				endTime = g.endTime;
			}
		}
		return endTime;
	}

	public int getNewGraphID() {
		return graphIDCounter++;
	}

	public int getNewIntervalID() {
//		TODO: is anything using this anymore?
		return intervalIDCounter++;
	}

	public int getNewNoteID() {
		return noteIDCounter++;
	}

	public ArrayList<Bounds> initBounds(ScoreWindow _window) {
		ArrayList<Bounds> boundsList = new ArrayList<Bounds>();

		for (ScoreItem.Type type : ScoreItem.TYPE_LIST) {
			switch (type) {
				case GRAPH:
					for (Graph g : graphList) {
						boundsList.add(((ScoreItem) g).createBounds(_window));
					}
				break;

				case NOTE:
					for (Note n : noteList) {
						boundsList.add(((ScoreItem) n).createBounds(_window));
					}
				break;

				case OVERTONES:
					for (Overtones o : overtonesList) {
						boundsList.add(((ScoreItem) o).createBounds(_window));
					}
				break;

				case TIME_MARKER:
					for (TimeMarker tm : timeMarkerList) {
						boundsList.add(((ScoreItem) tm).createBounds(_window));
					}
				break;

				case TONIC_BUTTON:
					for (TonicButton tb : tonicButtonList) {
						boundsList.add(((ScoreItem) tb).createBounds(_window));
					}
			}
		}

		return boundsList;
	}

	public void remove(ScoreItem _scoreItem) {
		switch (_scoreItem.type) {
			case GRAPH:
				graphList.remove(_scoreItem);
			break;

			case NOTE:
				noteList.remove(_scoreItem);
			break;

			case OVERTONES:
				overtonesList.remove(_scoreItem);
			break;

			case TIME_MARKER:
				timeMarkerList.remove(_scoreItem);
			break;

			case TONIC_BUTTON:
				tonicButtonList.remove(_scoreItem);
		}
	}

	public void resizeAllBounds(boolean _remakeDrawables) {
		for (ScoreWindow window : windowList) {
			window.resizeAllBounds(_remakeDrawables);
		}
	}

	public void setWindowList(ArrayList<ScoreWindow> _windowList) {
		windowList = _windowList;
		focusedWindow = windowList.get(0);
	}

	public void write(FileElement _e) throws CustomException {
		_e.add(AUTHOR, AUTHOR_VALUE);
		_e.add(NAME, name);
		_e.add(HARMONIC_CENTER, harmonicCenter);

		FileElement defaultRatiosE = _e.createChild(DEFAULT_RATIOS_TAG);
		for (Ratio r : defaultRatios) {
			r.write(defaultRatiosE.createChild(Ratio.TAG));
		}

		FileElement windowsListE = _e.createChild(WINDOW_LIST_TAG);
		for (ScoreWindow w : windowList) {
			w.write(windowsListE.createChild(ScoreWindow.TAG));
		}

		_e.add(GRAPH_ID_COUNTER, graphIDCounter);
		_e.add(INTERVAL_ID_COUNTER, intervalIDCounter);
		_e.add(NOTE_ID_COUNTER, noteIDCounter);

		FileElement graphListE = _e.createChild(GRAPH_LIST_TAG);
		for (Graph g : graphList) {
			g.write(graphListE.createChild(Graph.TAG));
		}

		FileElement noteListE = _e.createChild(NOTE_LIST_TAG);
		for (Note n : noteList) {
			n.write(noteListE.createChild(Note.TAG));
		}

		FileElement partListE = _e.createChild(INTERVAL_LIST_TAG);
		for (Part p : partList) {
			p.write(partListE.createChild(Part.TAG));
		}

		FileElement timeMarkerListE = _e.createChild(TIME_MARKER_LIST_TAG);
		for (TimeMarker tm : timeMarkerList) {
			tm.write(timeMarkerListE.createChild(TimeMarker.TAG));
		}

		FileElement tonicButtonListE = _e.createChild(TONIC_BUTTON_LIST_TAG);
		for (TonicButton tb : tonicButtonList) {
			tb.write(tonicButtonListE.createChild(TonicButton.TAG));
		}

	}

	@Override
	public void windowActivated(WindowEvent _e) {
	}

	@Override
	public void windowClosed(WindowEvent _e) {
	}

	@Override
	public void windowClosing(WindowEvent _e) {
		mainMenu.soundMenu.closeMidi();
	}

	@Override
	public void windowDeactivated(WindowEvent _e) {
	}

	@Override
	public void windowDeiconified(WindowEvent _e) {
	}

	@Override
	public void windowIconified(WindowEvent _e) {
	}

	@Override
	public void windowOpened(WindowEvent _e) {
	}


}
