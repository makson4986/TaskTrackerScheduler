package org.makson.tasktrackerscheduler.exceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException() {
        super();
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }
}
