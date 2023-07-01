package util;

public class CustomException extends Exception {

	private static final long serialVersionUID = 1L;


	public CustomException(String _location, String _message) {
		super(_location + " :EXC: " + _message);
		L.l(getMessage());
	}

	public CustomException(String _message) {
		super("---   :EXC: " + _message);
		L.l(getMessage());
	}

	public void print(boolean _printStackTrace) {
		L.l(getMessage());

		if (_printStackTrace) {
			printStackTrace();
		}
	}

}
