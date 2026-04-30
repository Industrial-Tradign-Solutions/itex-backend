package com.itradingsolutions.itex.api.masters.brand.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotImportBrandExcelException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -7783999104098863284L;

    public NotImportBrandExcelException() {
        super();
    }

    public NotImportBrandExcelException(String message) {
        super(message);
    }

    public NotImportBrandExcelException(Throwable cause) {
        super(cause);
    }

    public NotImportBrandExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NotImportBrandExcelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
