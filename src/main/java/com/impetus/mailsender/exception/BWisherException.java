package com.impetus.mailsender.exception;

public class BWisherException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BWisherException() {
        super();
    }

    public BWisherException(String msg) {
        super(msg);
    }

    public BWisherException(Throwable th) {
        super(th);
    }

    public BWisherException(String msg, Throwable th) {
        super(msg, th);
    }
}
