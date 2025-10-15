package exceptions;

public class InvalidLoanException extends Exception {
    public InvalidLoanException(String message) {
        super(message);
    }

    public InvalidLoanException(String message, Throwable cause) {
        super(message, cause);
    }
}