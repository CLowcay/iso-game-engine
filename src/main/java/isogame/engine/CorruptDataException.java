package isogame.engine;

public class CorruptDataException extends Exception {
	public CorruptDataException() {
		super();
	}

	public CorruptDataException(String msg) {
		super(msg);
	}

	public CorruptDataException(String msg, Exception cause) {
		super(msg, cause);
	}
}

