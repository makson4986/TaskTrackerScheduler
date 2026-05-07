package org.makson.tasktrackerscheduler.exceptions;

public class TemplateRetrievalException extends RuntimeException {
    public TemplateRetrievalException() {
        super();
    }

    public TemplateRetrievalException(String message) {
        super(message);
    }

    public TemplateRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateRetrievalException(Throwable cause) {
        super(cause);
    }
}
