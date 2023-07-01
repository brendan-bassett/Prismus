package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import note.Note;
import project.Part;
import project.Project;
import util.L;

@SuppressWarnings("serial")
public class PartManager extends JPanel {

	static final Font					FONT_14				= new Font("Dialog.bold", Font.PLAIN, 14);
	static final Font					FONT_16				= new Font("Dialog.bold", Font.PLAIN, 16);
	static final Font					FONT_18				= new Font("Dialog.bold", Font.PLAIN, 18);

	
	private final Project project;
	private final GridBagConstraints	managerConstraints	= new GridBagConstraints();
	private final ButtonGroup			nameButtonGroup		= new ButtonGroup();
	private final ArrayList<NameButton>	nameButtonList		= new ArrayList<NameButton>();


	public PartManager(Project _project) {
		project = _project;

		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEtchedBorder());

		managerConstraints.fill = GridBagConstraints.BOTH;
		managerConstraints.gridx = 0;
		managerConstraints.gridy = 0;
		managerConstraints.weightx = 7;
		managerConstraints.insets = new Insets(5, 5, 5, 3);
		managerConstraints.ipadx = 5;
		managerConstraints.ipady = 1;
		JLabel managerLabel = new JLabel("Part Manager");
		managerLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
		managerLabel.setHorizontalTextPosition(JLabel.CENTER);
//		TODO: figure out why this text alignment doesnt work
		managerLabel.setBackground(new Color(215, 215, 215));
		managerLabel.setFont(FONT_18);
		this.add(new JLabel("Part Manager"), managerConstraints);

		managerConstraints.fill = GridBagConstraints.BOTH;
		managerConstraints.gridx = 1;
		managerConstraints.weightx = 1;
		managerConstraints.ipadx = 5;
		managerConstraints.ipady = 1;
		managerConstraints.insets = new Insets(5, 0, 3, 5);
		this.add(new EditButton(), managerConstraints);

		managerConstraints.gridwidth = 2;
		managerConstraints.ipadx = 1;
		managerConstraints.ipady = 1;
		managerConstraints.insets = new Insets(2, 2, 2, 2);
		for (Part part : project.partList) {
			this.addPartToManager(part, false);
		}

		for (NameButton nameButton : nameButtonList) {
			if (nameButton.part.isSelected) {
				nameButton.doClick();
				break;
			}
		}

		this.repaint();
	}


	public void addPartToManager(Part _part, boolean _refreshAfter) {
		managerConstraints.gridy++;

		NameButton nameButton = new NameButton(_part);
		nameButtonList.add(nameButton);
		nameButtonGroup.add(nameButton);
		managerConstraints.gridx = 0;
		managerConstraints.fill = GridBagConstraints.BOTH;
		nameButton.setBackground(_part.color);

		this.add(nameButton, managerConstraints);

		if (_refreshAfter) {
			this.revalidate();
		}
	}

	public Part getSelectedPart() {
		for (NameButton nameButton : nameButtonList) {
			if (nameButton.isSelected()) {
				return nameButton.part;
			}
		}
		L.e("PartManager.getSelectedPart", "no part is selected!");
		return null;
	}

	public void removePartFromManager(Part _part) {
		for (int i = 0; i < nameButtonList.size(); i++) {
			NameButton nameButton = nameButtonList.get(i);
			if (nameButton.part == _part) {
				nameButtonList.remove(nameButton);
				this.remove(nameButton);
				if (nameButton.isSelected()) {
					nameButtonList.get(0).setSelected(true);
				}
			}
		}
		this.revalidate();
	}


	private class EditButton extends JButton implements ActionListener {

		EditButton() {
			super(">");

			this.addActionListener(this);
			this.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)));
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setOpaque(true);
			this.setBackground(new Color(215, 215, 215));
		}


		@Override
		public void actionPerformed(ActionEvent _e) {
			if (_e.getSource() == this) {
				L.l("PartManager.EditButton", "editButton action performed");

				PartEditorDialog partEditorDialog = new PartEditorDialog();
				partEditorDialog.setLocation((project.getWidth() / 2), (project.getHeight() / 2));
				partEditorDialog.setVisible(true);
			}
		}
	}

	private class NameButton extends JToggleButton implements ItemListener, MouseListener {

		private Part part;


		public NameButton(Part _part) {
			super(_part.name);
			part = _part;

			this.addItemListener(this);
			this.addMouseListener(this);
			this.setMargin(new Insets(0, 3, 0, 3));
			this.setPreferredSize(new Dimension(80, 25));

			if (part.isSelected) {
				this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				this.setFont(FONT_16);
				this.setSelected(true);
			} else {
				this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				this.setFont(FONT_14);
			}
		}


		@Override
		public void itemStateChanged(ItemEvent _event) {
//			TODO: get button to change color on mouseover even when selected
			if (_event.getStateChange() == ItemEvent.SELECTED) {
				this.setFont(FONT_16);
				this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				part.isSelected = true;
			} else if (_event.getStateChange() == ItemEvent.DESELECTED) {
				this.setFont(FONT_14);
				this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				part.isSelected = false;
			} else {
				L.e("PartManager.NameButton.itemStateChanged",
					"_event.getStateChange() !=_event.SELECTED && _event.getStateChange() != event.DESELECTED");
			}
			this.repaint();
		}


		@Override
		public void mouseClicked(MouseEvent _e) {
		}

		@Override
		public void mousePressed(MouseEvent _e) {
		}

		@Override
		public void mouseReleased(MouseEvent _e) {
		}

		@Override
		public void mouseEntered(MouseEvent _e) {
			this.setBackground(part.color.darker());
		}

		@Override
		public void mouseExited(MouseEvent _e) {
			this.setBackground(part.color);
		}
	}

	private class PartEditorDialog extends JDialog {

		Insets						colorButtonInsets		= new Insets(1, 5, 2, 3);
		Insets						deleteButtonInsets		= new Insets(2, 3, 2, 5);
		Insets						midiChannelFieldInsets	= new Insets(0, 2, 0, 2);
		Insets						partNameFieldInsets		= new Insets(0, 2, 0, 2);

		GridBagConstraints			dialogConstraints		= new GridBagConstraints();

		ArrayList<ColorButton>		colorButtonList			= new ArrayList<ColorButton>();
		ArrayList<DeletePartButton>	deletePartButtonList	= new ArrayList<DeletePartButton>();
		ArrayList<MidiChannelField>	midiChannelFieldList	= new ArrayList<MidiChannelField>();
		ArrayList<PartNameField>	partNameFieldList		= new ArrayList<PartNameField>();


		PartEditorDialog() {
			super(project, "Edit Parts");

			this.setLayout(new GridBagLayout());
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			dialogConstraints.fill = GridBagConstraints.HORIZONTAL;
			dialogConstraints.gridwidth = 3;
			dialogConstraints.gridy++;
			dialogConstraints.insets = new Insets(5, 5, 3, 5);
			dialogConstraints.anchor = GridBagConstraints.CENTER;

			AddPartButton newPartButton = new AddPartButton();
			this.add(newPartButton, dialogConstraints);

			dialogConstraints.gridwidth = 1;
			dialogConstraints.fill = GridBagConstraints.NONE;
			dialogConstraints.ipadx = 0;
			dialogConstraints.ipady = 0;
			for (Part part : project.partList) {
				addPartToDialog(part);
			}
		}


		private void addPartToDialog(Part _part) {
			dialogConstraints.gridy++;

			ColorButton colorButton = new ColorButton(_part);
			colorButtonList.add(colorButton);
			dialogConstraints.gridx = 0;
			dialogConstraints.insets = colorButtonInsets;
			dialogConstraints.insets = colorButtonInsets;
			this.add(colorButton, dialogConstraints);

			PartNameField partNameField = new PartNameField(_part);
			partNameFieldList.add(partNameField);
			dialogConstraints.gridx++;
			dialogConstraints.insets = partNameFieldInsets;
			this.add(partNameField, dialogConstraints);

			MidiChannelField midiChannelField = new MidiChannelField(_part);
			midiChannelFieldList.add(midiChannelField);
			dialogConstraints.gridx++;
			dialogConstraints.insets = midiChannelFieldInsets;
			this.add(midiChannelField, dialogConstraints);

			DeletePartButton deletePartButton = new DeletePartButton(_part);
			deletePartButtonList.add(deletePartButton);
			dialogConstraints.gridx++;
			dialogConstraints.insets = deleteButtonInsets;
			this.add(deletePartButton, dialogConstraints);

			this.pack(); // must stay at end of this method or new parts will not appear once added
		}

		private void removePartFromDialog(Part _part) {
			if (partNameFieldList.size() == 0) {
				JOptionPane.showMessageDialog(this, "Project needs at least one part. Add a new one before deleting this one.");

			} else {
				for (ColorButton colorButton : colorButtonList) {
					if (colorButton.part == _part) {
						colorButtonList.remove(colorButton);
						this.remove(colorButton);
						break;
					}
				}
				for (DeletePartButton deletePartButton : deletePartButtonList) {
					if (deletePartButton.part == _part) {
						deletePartButtonList.remove(deletePartButton);
						this.remove(deletePartButton);
						break;
					}
				}
				for (MidiChannelField midiChannelField : midiChannelFieldList) {
					if (midiChannelField.part == _part) {
						midiChannelFieldList.remove(midiChannelField);
						this.remove(midiChannelField);
						break;
					}
				}
				for (PartNameField partNameField : partNameFieldList) {
					if (partNameField.part == _part) {
						partNameFieldList.remove(partNameField);
						this.remove(partNameField);
						break;
					}
				}

				dialogConstraints.gridy--;
				this.repaint();
				this.pack();
			}
		}


		class AddPartButton extends JButton implements ActionListener {

			AddPartButton() {
				super("Add New Part...");

				this.addActionListener(this);
			}


			@Override
			public void actionPerformed(ActionEvent _event) {
				int midiChannel = 0;
				for (Part p : project.partList) {
					if (midiChannel == p.midiChannel) {
						midiChannel++;
						break;
					}
				}

				int newPartNumber = 0;
				Part newPart = new Part(midiChannel, false, "Part " + newPartNumber);

				boolean nameValid = false;
				keepChecking: while (!nameValid) {
					for (Part part : project.partList) {
						if (newPart.name.matches(part.name)) {
							newPartNumber++;
							newPart.name = "Part " + newPartNumber;
							continue keepChecking;
						}
					}
					nameValid = true;
				}

				project.partList.add(newPart);
				addPartToDialog(newPart);
				addPartToManager(newPart, true);
			}
		}

		class ColorButton extends JButton implements ActionListener {

			private Part part;


			public ColorButton(Part _part) {
				part = _part;

				this.setBackground(part.color);
				this.setPreferredSize(new Dimension(30, 30));
				this.addActionListener(this);
			}


			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(this, "Choose a color", part.color);
				part.color = color;
				this.setBackground(color);

				for (NameButton nameButton : nameButtonList) {
					if (nameButton.part == part) {
						nameButton.setBackground(color);
						break;
					}
				}

				project.repaint();
			}
		}

		class DeletePartButton extends JButton implements ActionListener {

			final Part part;


			DeletePartButton(Part _part) {
				part = _part;

				this.addActionListener(this);
				this.setPreferredSize(new Dimension(45, 30));
				this.setFont(FONT_14);
				this.setText("X");
			}


			@Override
			public void actionPerformed(ActionEvent _e) {
				boolean notesLeftInPart = false;
				for (Note note : project.noteList) {
					if (note.part == part) {
						notesLeftInPart = true;
						break;
					}
				}

				if (notesLeftInPart) {
					JOptionPane.showMessageDialog(this, "There are notes left with this part. Reassign them before deleting this.");
				} else {
					project.partList.remove(part);
					removePartFromDialog(part);
					removePartFromManager(part);
				}
			}

		}

		class MidiChannelField extends JTextField implements KeyListener, FocusListener {

			final Part part;


			MidiChannelField(Part _part) {
				part = _part;

				this.setText("" + part.midiChannel);
				this.setPreferredSize(new Dimension(30, 30));
				this.setHorizontalAlignment(JTextField.CENTER);
				this.addFocusListener(this);
				this.addKeyListener(this);
			}


			@Override
			public void focusGained(FocusEvent _e) {
			}

			@Override
			public void focusLost(FocusEvent _event) {
				this.changeMidiChannel();
			}

			@Override
			public void keyTyped(KeyEvent _event) {
				if (_event.getKeyChar() == KeyEvent.VK_ENTER) {
					this.changeMidiChannel();
				}
			}

			@Override
			public void keyPressed(KeyEvent _e) {
			}

			@Override
			public void keyReleased(KeyEvent _e) {
			}


			private void changeMidiChannel() {
				try {
					int newChannel = Integer.parseInt(this.getText());
					if (part.midiChannel != newChannel) {
						if (newChannel < 0 || newChannel > 15) {
							this.setText("" + part.midiChannel);
							JOptionPane.showMessageDialog(this, "Midi channel must be from 0 to 15");

						} else {
							boolean isValid = true;
							for (Part p : project.partList) {
								if (newChannel == p.midiChannel) {
									this.setText("" + part.midiChannel);
									JOptionPane.showMessageDialog(this, "That midi channel is already assigned to another part");
									isValid = false;
									break;
								}
							}

							if (isValid) {
								part.midiChannel = newChannel;
							}
						}
					}

				} catch (Exception e) {
					this.setText("" + part.midiChannel);
					JOptionPane.showMessageDialog(this, "Midi channel must be an integer");
				}
			}

		}

		class PartNameField extends JTextField implements KeyListener, FocusListener {

			final Part part;


			PartNameField(Part _part) {
				part = _part;

				this.setText(part.name);
				this.setPreferredSize(new Dimension(80, 30));
				this.setHorizontalAlignment(JTextField.CENTER);
				this.addFocusListener(this);
				this.addKeyListener(this);
			}


			@Override
			public void focusGained(FocusEvent _e) {
			}

			@Override
			public void focusLost(FocusEvent _event) {
				changePartName(this.getText());
			}

			@Override
			public void keyTyped(KeyEvent _event) {
				if (_event.getKeyChar() == KeyEvent.VK_ENTER) {
					changePartName(this.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent _e) {
			}

			@Override
			public void keyReleased(KeyEvent _e) {
			}


			private void changePartName(String _name) {
				String newName = _name.trim();

				if (!part.name.matches(newName)) {
					boolean isValid = true;
					for (Part p : project.partList) {
						if (newName.matches(p.name)) {
							this.setText(part.name);
							JOptionPane.showMessageDialog(this, "A part already has that name.");
							isValid = false;
							break;
						}
					}

					if (isValid) {
						part.name = newName;
						for (NameButton nameButton : nameButtonList) {
							if (nameButton.part == part) {
								nameButton.setText(part.name);
								break;
							}
						}
					}
				}
			}

		}

	}


}
