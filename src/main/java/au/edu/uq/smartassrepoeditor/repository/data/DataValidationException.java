package au.edu.uq.smartassrepoeditor.repository.data;

public class DataValidationException extends RuntimeException{

	public DataValidationException() {
	}

	public DataValidationException(String message) {
		super(message);
	}
}
