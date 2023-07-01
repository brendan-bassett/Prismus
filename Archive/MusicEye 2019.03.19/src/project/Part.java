package project;

import java.awt.Color;

import file.FILE;
import file.FileElement;
import util.CustomException;

public class Part implements FILE.PART_F {

	static public Part read(Project _project, FileElement _e) throws CustomException {
		Color color = new Color(_e.get(COLOR_RGB));
		int id = _e.get(ID);
		boolean isSelected = _e.get(IS_SELECTED);
		String name = _e.get(NAME);

		return new Part(color, id, isSelected, name);
	}


	public Color	color		= new Color(181, 237, 255);
	public int		midiChannel	= 0;
	public boolean	isSelected	= false;
	public String	name;


	/**
	 * @param _midiChannel
	 * @param _isSelected
	 * @param _name
	 */
	public Part(int _midiChannel, boolean _isSelected, String _name) {
		midiChannel = _midiChannel;
		isSelected = _isSelected;
		name = _name;
	}

	/**
	 * @param _color
	 * @param _midiChannel
	 * @param _isSelected
	 * @param _name
	 */
	public Part(Color _color, int _midiChannel, boolean _isSelected, String _name) {
		color = _color;
		midiChannel = _midiChannel;
		isSelected = _isSelected;
		name = _name;
	}


	public void write(FileElement _e) throws CustomException {
		_e.add(COLOR_RGB, color.getRGB());
		_e.add(ID, midiChannel);
		_e.add(IS_SELECTED, isSelected);
		_e.add(NAME, name);
	}

}
