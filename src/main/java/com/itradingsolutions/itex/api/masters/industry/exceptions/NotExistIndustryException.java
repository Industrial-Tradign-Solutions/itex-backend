package com.itradingsolutions.itex.api.masters.industry.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistIndustryException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 3253721528022035082L;

    public NotExistIndustryException(String message) {
        super(message);
    }
}
