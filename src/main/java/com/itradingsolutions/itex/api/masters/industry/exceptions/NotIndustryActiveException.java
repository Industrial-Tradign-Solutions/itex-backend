package com.itradingsolutions.itex.api.masters.industry.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotIndustryActiveException extends NotFoundException {
  @Serial
  private static final long serialVersionUID = -7355610395451108066L;

  public NotIndustryActiveException(String message) {
        super(message);
    }
}
