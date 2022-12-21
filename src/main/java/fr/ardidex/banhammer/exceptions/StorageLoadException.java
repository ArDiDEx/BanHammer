package fr.ardidex.banhammer.exceptions;

public class StorageLoadException extends Exception {
    public StorageLoadException() {
    }

    public StorageLoadException(Throwable cause) {
        super(cause);
    }

    public StorageLoadException(String message) {
        super(message);
    }
}
