package com.itradingsolutions.itex.api.common.util.exceptions;

import java.io.Serial;

public class BadRequestException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 2596629669564084508L;

  public BadRequestException(String message) {
        super(message);
    }
}
