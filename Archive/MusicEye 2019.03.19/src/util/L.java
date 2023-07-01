package util;

public abstract class L {

	/**
	 * 
	 * @param _location
	 * @param _message
	 */
	static public void l(String _location, String _message) {
		System.out.println(_location + "() :: " + _message);
	}


	/**
	 * @param _location
	 */
	static public void l(String _location) {
		System.out.println(_location + "()");
	}

	/**
	 * 
	 * @param _location
	 * @param _message
	 */
	static public void l(String _location, int _message) {
		System.out.println(_location + "() :: " + _message);
	}

	/**
	 * @param _location
	 */
	static public void e(String _location) {
		System.out.println(" !!! " + _location + "() !!!");
	}

	/**
	 * 
	 * @param _location
	 * @param _message
	 */
	static public void e(String _location, String _message) {
		System.out.println("!!! " + _location + "() !!! " + _message + " !!!");
	}

	/**
	 * @param _location
	 */
	static public void q(String _location) {
		System.out.println(" ?? " + _location + "() ??");
	}

	/**
	 * 
	 * @param _location
	 * @param _message
	 */
	static public void q(String _location, String _message) {
		System.out.println("?? " + _location + "() ?? " + _message + " ??");
	}

}
