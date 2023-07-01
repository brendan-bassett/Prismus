package file;



public class StringAttr extends AbstractAttr {

	protected StringAttr(String _name) {
		super(_name);
	}


	protected String get(String _string) {
		return _string;
	}

	protected void set(String _value) {
		value = _value;
	}

}
