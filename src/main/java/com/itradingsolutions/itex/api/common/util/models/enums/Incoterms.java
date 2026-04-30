package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum Incoterms implements BaseEnum {
    EXW("Ex Works", "En Fábrica"),
    FCA("Free Carrier", "Franco Transportista"),
    CPT("Carriage Paid To", "Transporte Pagado Hasta"),
    CIP("Carriage and Insurance Paid To", "Transporte y Seguro Pagados Hasta"),
    DAP("Delivered At Place", "Entregado en Lugar"),
    DPU("Delivered at Place Unloaded", "Entregado en Lugar Descargado"),
    DDP("Delivered Duty Paid", "Entrega Derechos Pagados"),
    FAS("Free Alongside Ship", "Franco al Costado del Buque"),
    FOB("Free On Board", "Franco a Bordo"),
    CFR("Cost and Freight", "Coste y Flete"),
    CIF("Cost, Insurance and Freight", "Coste, Seguro y Flete"),
    ;
    private final String name;
    private final String spanishName;
    Incoterms(final String name, final String spanishName) {
        this.name = name;
        this.spanishName = spanishName;
    }
}
