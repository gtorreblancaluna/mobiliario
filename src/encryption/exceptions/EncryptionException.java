package encryption.exceptions;

public class EncryptionException extends Exception {

    private transient Throwable cause;
    private transient String message;

    public EncryptionException() {
        super();
    }

    public EncryptionException(final String message) {
        this.message = message;
    }

    public EncryptionException(final String message, final Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
