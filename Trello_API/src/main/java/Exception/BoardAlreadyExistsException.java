package Exception;

public class BoardAlreadyExistsException extends RuntimeException{
	public BoardAlreadyExistsException (String message) {
		super(message);
	}
}
