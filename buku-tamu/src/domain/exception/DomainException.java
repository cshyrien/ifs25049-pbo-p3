package domain.exception;


/**
 * Checked exception dasar untuk hierarki domain model aplikasi.
 * Semua kegagalan bisnis (validasi, data tidak ditemukan) diturunkan dari sini.
 */
public class DomainException extends Exception {
    private static final long serialVersionUID = 1L;


    public DomainException(String message) {
        super(message);
    }


    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
