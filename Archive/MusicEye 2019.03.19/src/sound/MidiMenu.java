package sound;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

import note.Note;
import project.Part;
import project.Project;
import util.CustomException;
import util.L;
import util.K;

public class MidiMenu extends JMenu implements MouseListener {

	private static final byte		BLANK_BYTE			= 0x7F;
	private static final int		DATA_SHIFT			= 7;
	private static final long		DEFAULT_TIME_STAMP	= -1;
	private static final float		DEFAULT_VELOCITY	= 0.8f;
	private static final int		DEFAULT_BPM			= 60;
	private static final float		MID_A_HZ			= 440;
	private static final int		MID_A_KEY			= 69;
	private static final String		NAME				= "MIDI Output";
	private static final float		PITCH_BEND_RANGE	= 2f;
	private static final int		TICKS_PER_QUARTER	= 128;
	private static final int		TEST_OFF_DELAY		= 9000;
	private static final float		TEST_VELOCITY		= 0.8f;


	private static final long		serialVersionUID	= 1L;


	private final Project			project;

	private MidiDevice.Info[]		deviceInfoList;
	private MidiDevice				outputDevice;
	private Receiver				outputReceiver;

	private final ButtonGroup		outputGroup			= new ButtonGroup();
	private ArrayList<OutputButton>	outputButtonList	= new ArrayList<OutputButton>();

	private Sequence				sequence;
	private Sequencer				sequencer;
	private Transmitter				sequencerTransmitter;


//	FIXME:synchronize playing in gui and midi

	MidiMenu(Project _project) {
		super(NAME);

		project = _project;

		addMouseListener(this);
	}


	@Override
	public void mouseClicked(MouseEvent _e) {
	}

	@Override
	public void mouseEntered(MouseEvent _e) {
		deviceInfoList = MidiSystem.getMidiDeviceInfo();
		ArrayList<OutputButton> newOutputButtonList = new ArrayList<OutputButton>();

		d: for (int i = 0; i < deviceInfoList.length; i++) {
			MidiDevice.Info info = deviceInfoList[i];

			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);

				if (device.getMaxReceivers() == 0) {
					continue;
				}

				for (int j = 0; j < outputButtonList.size(); j++) {
					OutputButton outputButton = outputButtonList.get(j);
					if (outputButton.device == device) {
						newOutputButtonList.add(outputButton);
						outputButtonList.remove(outputButton);
						continue d;
					}
				}

				OutputButton outputButton = new OutputButton(device, info);

				newOutputButtonList.add(outputButton);
				add(outputButton);
				outputGroup.add(outputButton);

			} catch (MidiUnavailableException exception) {
				exception.printStackTrace();
			}
		}

		for (int i = 0; i < outputButtonList.size(); i++) {
			OutputButton outputButton = outputButtonList.get(i);
			outputGroup.remove(outputButton);
			remove(outputButton);
		}

		outputButtonList = newOutputButtonList;

	}

	@Override
	public void mouseExited(MouseEvent _e) {
	}

	@Override
	public void mousePressed(MouseEvent _e) {
	}

	@Override
	public void mouseReleased(MouseEvent _e) {
	}

	public void playNote(Part _part, float _hz, float _endDelay, float _velocity) {
		if (outputReceiver != null) {
			try {
//				float _eT, float _hz, Part _part, float _sT, float _velocity
				MidiNote midiNote = new MidiNote(_hz, _part, _velocity);

				outputReceiver.send(midiNote.getPitchBendMessage(), DEFAULT_TIME_STAMP);
				outputReceiver.send(midiNote.getNoteOnMessage(), DEFAULT_TIME_STAMP);

				int endDelay = Math.round(_endDelay * 1000);
				Timer timer = new Timer(endDelay, new EndListener(midiNote));
				timer.setRepeats(false);
				timer.start();

			} catch (InvalidMidiDataException exception) {
				exception.printStackTrace();
			}

		}
	}

	public void printDevices() {
		for (int i = 0; i < deviceInfoList.length; i++) {
			L.l("MIDI.printDevices()", "name" + i + " " + deviceInfoList[i].getName());
		}
	}


	void close() {
//		TODO: debug program freeze at close

		if (outputDevice != null) {
			outputDevice.close();
		}

		if (sequencer != null) {
			sequencer.stop();
			sequencer.close();
		}

		if (sequencerTransmitter != null) {
			sequencerTransmitter.close();
		}
	}

	long getSequencerPosition() {
		return sequencer.getMicrosecondPosition();
	}

	void play() throws CustomException, InvalidMidiDataException, MidiUnavailableException {
		if (outputReceiver == null) {
			throw new CustomException("MidiMenu.play()", "outputReceiver == null");
		}
		if (sequencer == null) {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();

			sequencerTransmitter = sequencer.getTransmitter();
			sequencerTransmitter.setReceiver(outputReceiver);
		}
		sequencer.setTempoInBPM(DEFAULT_BPM);

		try {
			sequence = new Sequence(Sequence.PPQ, TICKS_PER_QUARTER, project.partList.size());
		} catch (InvalidMidiDataException exception) {
			L.e("MidiMenu", "error creating new Sequence");
			exception.printStackTrace();
		}

		Track[] trackArray = sequence.getTracks();
		for (int i = 0; i < trackArray.length; i++) {
			Track track = trackArray[i];
			Part part = project.partList.get(i);

			if (track != null && part != null) {
				for (Note note : project.noteList) {

					if (note.part == part) {
						try {

							MidiSequence midiSequence = new MidiSequence(note.endT, note.hz, note.part, note.startT, DEFAULT_VELOCITY);
							track.add(new MidiEvent(midiSequence.getPitchBendMessage(), midiSequence.pbTick));
							track.add(new MidiEvent(midiSequence.getNoteOnMessage(), midiSequence.onTick));
							track.add(new MidiEvent(midiSequence.getNoteOffMessage(), midiSequence.offTick));

//							L.l("MidiMenu.play()",
//								"midiSequence.channel==" + midiSequence.channel + " midiSequence.key==" + midiSequence.key);

						} catch (InvalidMidiDataException exception) {
							exception.printStackTrace();
						}
					}
				}

			} else {
				L.e("MidiMenu.play", "track==null || part==null");
			}
		}

		sequencer.setSequence(sequence);
		sequencer.start();
	}

	void pause() {
		if (sequencer != null) {
			sequencer.stop();
		}
	}

	void stop() {
		if (sequencer != null) {
			sequencer.stop();
			sequencer.setTickPosition(0);
		}
	}


	private class MidiNote {

		final int	channel;
		final int	key;
		final int	pitchData1;
		final int	pitchData2;
		final int	velocity;


		MidiNote(float _hz, Part _part, float _velocity) {
			float keyD = (12 * (float) (Math.log(_hz / MID_A_HZ) / Math.log(2)) + MID_A_KEY);
			key = Math.round(keyD);

			float keyHZ = MID_A_HZ * (float) Math.pow((Math.pow(2, K.SEMITONE_FRACTION)), (key - MID_A_KEY));
			float nD = K.SEMITONES_PER_OCTAVE / 2 * K.TWO_POW_13 * K.relPFromHz(_hz, keyHZ) + K.TWO_POW_13;
			int b = Math.round(nD);

			channel = _part.midiChannel;
			velocity = Math.round(_velocity * K.TWO_POW_7);

			pitchData1 = (byte) (b & BLANK_BYTE);
			pitchData2 = (byte) ((b >> DATA_SHIFT) & BLANK_BYTE);

			L.l("MidiMenu.MidiNote", "channel==" + channel + " b==" + b + " nD==" + nD + " nD%==" + (((nD - 8192) / 8192)) * 100);
		}


		ShortMessage getNoteOffMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.NOTE_OFF, channel, key, velocity);
		}

		ShortMessage getNoteOnMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.NOTE_ON, channel, key, velocity);
		}

		ShortMessage getPitchBendMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.PITCH_BEND, channel, pitchData1, pitchData2);
		}

	}

	/**
	 * @author Brendan
	 *
	 */
	private class MidiSequence extends MidiMenu.MidiNote {

		final long	offTick;
		final long	pbTick;
		final long	onTick;


		/**
		 * @param _eT
		 * @param _hz
		 * @param _part
		 * @param _sT
		 * @param _velocity
		 */
		MidiSequence(float _eT, float _hz, Part _part, float _sT, float _velocity) {
			super(_hz, _part, _velocity);

			offTick = (long) (_eT * TICKS_PER_QUARTER * DEFAULT_BPM) / K.SECONDS_PER_MINUTE;
			onTick = (long) (_sT * TICKS_PER_QUARTER * DEFAULT_BPM) / K.SECONDS_PER_MINUTE;
			pbTick = onTick - 1;
		}


		ShortMessage getNoteOffMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.NOTE_OFF, channel, key, velocity);
		}

		ShortMessage getNoteOnMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.NOTE_ON, channel, key, velocity);
		}

		ShortMessage getPitchBendMessage() throws InvalidMidiDataException {
			return new ShortMessage(ShortMessage.PITCH_BEND, channel, pitchData1, pitchData2);
		}

	}


	private class OutputButton extends JRadioButtonMenuItem implements ActionListener {

		/**
		 * 
		 */
		private static final long		serialVersionUID	= 1L;
		private final MidiDevice		device;
		private final MidiDevice.Info	info;

		private Receiver				receiver;


		OutputButton(MidiDevice _device, MidiDevice.Info _info) {
			super(_info.getName());
			device = _device;
			info = _info;

			setToolTipText(info.getVendor() + " : " + info.getDescription());
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent _e) {
			if (outputDevice != null && outputDevice.isOpen()) {
				outputDevice.close();
			}

			if (outputReceiver != null) {
				outputReceiver.close();
			}

			if (!device.isOpen()) {
				try {
					device.open();
					outputDevice = device;

					L.l("MidiMenu.OutputButton.actionPerformed", "outputDevice set to " + outputDevice.getDeviceInfo().getName());

				} catch (MidiUnavailableException _exception) {
					_exception.printStackTrace();
				}
			}

			if (receiver == null) {
				try {
					receiver = device.getReceiver();
					outputReceiver = receiver;

				} catch (MidiUnavailableException _exception) {
					_exception.printStackTrace();
				}
			}
		}

	}


	private class EndListener implements ActionListener {

		private final MidiNote midiNote;


		EndListener(MidiNote _midiNote) {
			midiNote = _midiNote;
		}

		@Override
		public void actionPerformed(ActionEvent _arg0) {
			try {
				outputReceiver.send(midiNote.getNoteOffMessage(), DEFAULT_TIME_STAMP);
				L.l("MidiMenu.EndListener.actionPerformed", "note off message sent");

			} catch (InvalidMidiDataException exception) {
				exception.printStackTrace();
			}
		}
	}


}
