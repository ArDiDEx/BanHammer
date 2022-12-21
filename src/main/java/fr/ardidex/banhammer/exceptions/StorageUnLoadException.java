package fr.ardidex.banhammer.exceptions;

public class StorageUnLoadException extends Exception {
    public StorageUnLoadException() {
    }

    public StorageUnLoadException(Throwable cause) {
        super(cause);
    }

    public StorageUnLoadException(String message) {
        super(message);
    }
}
