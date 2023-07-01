package file;

import util.CustomException;


public class BooleanAttr extends AbstractAttr {

	static protected final String	TRUE	= "T";
	static protected final String	FALSE	= "F";


	protected BooleanAttr(String _name) {
		super(_name);
	}


	protected boolean get(String _string) throws CustomException {
		if (_string.matches(TRUE)) {
			return true;
		} else if (_string.matches(FALSE)) {
			return false;
		} else {
			throw new CustomException("s.matches(TRUE) == false && s.matches(FALSE) == false");
		}
	}

	protected void set(boolean _value) {
		if (_value) {
			value = TRUE;
		} else {
			value = FALSE;
		}
	}

}
