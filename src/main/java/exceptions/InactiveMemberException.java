package exceptions;

public class InactiveMemberException extends Exception {
    public InactiveMemberException(String message) {
        super(message);
    }

    public InactiveMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}