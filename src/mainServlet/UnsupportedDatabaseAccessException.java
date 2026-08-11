package mainServlet;

public class UnsupportedDatabaseAccessException extends Exception {

	private static final long serialVersionUID = -576068682091235549L;

	public UnsupportedDatabaseAccessException() {
		super();
	}

	public UnsupportedDatabaseAccessException(String arg0) {
		super(arg0);
	}
}
