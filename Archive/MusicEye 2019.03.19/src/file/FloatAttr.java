package file;

import util.CustomException;


public class FloatAttr extends AbstractAttr {

	protected FloatAttr(String _name) {
		super(_name);
	}


	protected float get(String _string) throws CustomException {
		return Float.parseFloat(_string);
	}

	protected void set(float _value) {
		value = Float.toString(_value);
	}

}
