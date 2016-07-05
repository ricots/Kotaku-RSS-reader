/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package com.oskalenko.kotakurss.exception;

/**
 * Exception throw by the application when a there is a network connection exception.
 */
public class NetworkConnectionException extends RuntimeException {

  public NetworkConnectionException() {
    super();
  }

  public NetworkConnectionException(final String message) {
    super(message);
  }

  public NetworkConnectionException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public NetworkConnectionException(final Throwable cause) {
    super(cause);
  }
}
