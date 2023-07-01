package file;

import util.CustomException;


public class IntAttr extends AbstractAttr {

	protected IntAttr(String _name) {
		super(_name);
	}


	protected int get(String _string) throws CustomException {
		return Integer.parseInt(_string);
	}

	protected void set(int _value) {
		value = Integer.toString(_value);
	}

}
