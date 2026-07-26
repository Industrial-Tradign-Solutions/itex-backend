package com.itradingsolutions.itex.api.common.jasper.model.enums;

import lombok.Getter;

@Getter
public enum JasperReport {
    IP_QR_EN("IP/QR/IP_QR_EN.jasper"),
    IP_QR_ES("IP/QR/IP_QR_ES.jasper"),
    IP_Q_EN("IP/Q/IP_Q_EN.jasper"),
    IP_Q_ES("IP/Q/IP_Q_ES.jasper"),
    IP_PO_EN("IP/PO/IP_PO_EN.jasper"),
    IP_PO_ES("IP/PO/IP_PO_ES.jasper")
    ;

    private final String path;

    JasperReport(final String path) {
        this.path = path;
    }
}
