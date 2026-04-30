package com.itradingsolutions.itex.api.masters.brand.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotBrandActiveException extends NotFoundException {
  @Serial
  private static final long serialVersionUID = -7355610395451108066L;

  public NotBrandActiveException(String message) {
        super(message);
    }
}
