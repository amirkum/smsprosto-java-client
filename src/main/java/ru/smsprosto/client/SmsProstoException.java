package ru.smsprosto.client;

public class SmsProstoException extends RuntimeException {

    private final int errorCode;
    private final Integer httpStatus;

    public SmsProstoException(String message) {
        this(message, -1, null, null);
    }

    public SmsProstoException(String message, int errorCode, Integer httpStatus) {
        this(message, errorCode, httpStatus, null);
    }

    public SmsProstoException(String message, int errorCode,
                              Integer httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public int getErrorCode() { return errorCode; }
    public Integer getHttpStatus() { return httpStatus; }
}
