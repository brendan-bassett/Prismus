package sound;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.sun.glass.events.KeyEvent;

import gui.STYLE;
import project.Part;
import project.Project;
import util.CustomException;

@SuppressWarnings("serial")
public class SoundMenu extends JMenu implements STYLE.SOUND_MENU {

	static private final String					NAME				= "Sound";

	static private final String					PLAY_NAME			= "PLAY";
	static private final String					PAUSE_NAME			= "Pause...";
	static private final String					STOP_NAME			= "STOP";
	static private final String					MIDI_TEST_NAME		= "Send MIDI Test Note";

	static private final int					DEFAULT_START_TIME	= 0;
	static private final int					REFRESH_RATE		= 5;


	public final PlayMarker						playMarker;

	MidiMenu									midiMenu;
	JMenuItem									midiTestItem		= new JMenuItem(MIDI_TEST_NAME);

	JMenuItem									playItem			= new JMenuItem(PLAY_NAME, KeyEvent.VK_P);
	JMenuItem									pauseItem			= new JMenuItem(PAUSE_NAME);
	JMenuItem									stopItem			= new JMenuItem(STOP_NAME);

	private final Project						project;

	final ArrayList<PlayMarker.PlayingAction>	playingActionList	= new ArrayList<PlayMarker.PlayingAction>();
	float										playingTime, resetTime;

	private float								endTime, pauseTime;												//in seconds
	private double								currentTimeStamp, refTimeStamp;									//in seconds
	private Timer								timer;


	public SoundMenu(Project _project) {
		super(NAME);
		project = _project;

		endTime = project.getEndTime();
		pauseTime = DEFAULT_START_TIME;
		playingTime = DEFAULT_START_TIME;
		resetTime = DEFAULT_START_TIME;

		ButtonListener bListener = new ButtonListener();

		midiMenu = new MidiMenu(project);
		add(midiMenu);

		midiTestItem.addActionListener(bListener);
		add(midiTestItem);

		playItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		playItem.addActionListener(bListener);
		add(playItem);

		pauseItem.addActionListener(bListener);
		pauseItem.setEnabled(false);
		add(pauseItem);

		stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		stopItem.addActionListener(bListener);
		stopItem.setEnabled(false);
		add(stopItem);

		timer = new Timer(REFRESH_RATE, new TimerListener());

		playMarker = new PlayMarker(project, this);
		playMarker.initAdd();
	}


	public void closeMidi() {
		midiMenu.close();
	}

	public void playNote(Part _part, float _hz, int _endDelay, float _velocity) {
//		midiMenu.playNote(_part, _hz, _endDelay, _velocity);
	}


	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent _event) {
			Object source = _event.getSource();

			if (source == midiTestItem) {
//				midiMenu.playTestNote(project.toolBar.partManager.getSelectedPart(), 440);

			} else if (source == playItem) {
				if (!stopItem.isEnabled()) { //start from beginning
					playItem.setEnabled(false);
					pauseItem.setEnabled(true);
					stopItem.setEnabled(true);

					endTime = project.getEndTime();
					pauseTime = DEFAULT_START_TIME;
					playingTime = DEFAULT_START_TIME;
					resetTime = DEFAULT_START_TIME;

					currentTimeStamp = ((double) System.currentTimeMillis()) / 1000;
					refTimeStamp = currentTimeStamp;

				} else { //start @ pause point
					pauseItem.setEnabled(true);
					playItem.setEnabled(false);

					currentTimeStamp = ((double) System.currentTimeMillis()) / 1000;
					refTimeStamp = currentTimeStamp;
				}

				playMarker.startPlayingAction();
				timer.start();

				try {
					midiMenu.play();
				} catch (MidiUnavailableException | InvalidMidiDataException _e) {
					_e.printStackTrace();
				} catch (CustomException _e) {
					_e.print(true);
				}

			} else if (source == pauseItem) {
				pauseItem.setEnabled(false);
				playItem.setEnabled(true);

				currentTimeStamp = ((double) System.currentTimeMillis()) / 1000;
				playingTime = (float) (currentTimeStamp - refTimeStamp) + pauseTime;

				timer.stop();
				pauseTime = playingTime;
				refTimeStamp = currentTimeStamp;

				midiMenu.pause();

			} else if (source == stopItem) {
				if (pauseItem.isEnabled()) {
					pauseItem.setEnabled(false);
				}

				stopItem.setEnabled(false);
				playItem.setEnabled(true);

				timer.stop();
				pauseTime = resetTime;
				playingTime = resetTime;

				for (int i = 0; i < playingActionList.size(); i++) {
					playingActionList.get(i).cancel();
				}
				playingActionList.clear();

				midiMenu.stop();
			}

			project.repaint();
		}
	}

	private class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent _event) {
			currentTimeStamp = ((double) System.currentTimeMillis()) / 1000;
			playingTime = ((float) (currentTimeStamp - refTimeStamp)) + pauseTime;

			if (playingTime >= endTime) {
				stopItem.doClick();

			} else {
				for (int i = 0; i < playingActionList.size(); i++) {
					playingActionList.get(i).resize();
				}
				project.repaint();
			}
		}

	}

}
