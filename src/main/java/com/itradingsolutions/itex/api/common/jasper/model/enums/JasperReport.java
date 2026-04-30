package com.itradingsolutions.itex.api.common.jasper.model.enums;

import lombok.Getter;

@Getter
public enum JasperReport {
    IP_QR_EN("IP/QR/IP_QR_EN.jasper"),
    IP_QR_ES("IP/QR/IP_QR_ES.jasper")
    ;

    private final String path;

    JasperReport(final String path) {
        this.path = path;
    }
}
