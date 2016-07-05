package com.oskalenko.kotakurss.exception;

public class NoNetworkException extends Exception {

    public NoNetworkException() {
        super();
    }

    public NoNetworkException(String detailMessage) {
        super(detailMessage);
    }

    public NoNetworkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoNetworkException(Throwable throwable) {
        super(throwable);
    }
}
