package com.lingocoder.jar;

public class UnexecutableJarException extends RuntimeException {

    private static final long serialVersionUID = -1847253784521396894L;

    public UnexecutableJarException() {
        super();
    }

    public UnexecutableJarException(String message) {
        super(message);
    }

    public UnexecutableJarException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexecutableJarException(Throwable cause) {
        super(cause);
    }

    protected UnexecutableJarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
