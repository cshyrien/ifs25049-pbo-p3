package domain.exception;


/**
 * Exception yang dilempar saat terjadi kegagalan validasi aturan bisnis atau input domain.
 */
public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;


    public ValidationException(String message) {
        super(message);
    }


    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
