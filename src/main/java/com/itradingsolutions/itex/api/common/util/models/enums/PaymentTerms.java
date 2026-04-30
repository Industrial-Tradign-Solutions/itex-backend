package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum PaymentTerms implements BaseEnum{
    NET_5("NET 5"),
    NET_7("NET 7"),
    NET_8("NET 8"),
    NET_10("NET 10"),
    NET_14("NET 14"),
    NET_15("NET 15"),
    NET_20("NET 20"),
    NET_21("NET 21"),
    NET_30("NET 30"),
    NET_35("NET 35"),
    NET_40("NET 40"),
    NET_45("NET 45"),
    NET_55("NET 55"),
    NET_60("NET 60"),
    NET_75("NET 75"),
    NET_90("NET 90"),
    NET_120("NET 120"),
    NET_150("NET 150"),
    NET_180("NET 180"),
    NET_15TH_PROX("NET 15TH PROX"),
    NET_20TH_PROX("NET 20TH PROX"),
    NET_30TH_PROX("NET 30TH PROX"),
    NET_30_END_OF_THE_MONTH("NET 30, END OF THE MONTH"),
    NET_60_END_OF_THE_MONTH("NET 60, END OF THE MONTH"),
    ADVANCED("ADVANCED"),
    COD("COD"),
    DUE_UPON_RECEIPT("DUE UPON RECEIPT"),
    PRIOR_TO_SHIPMENT("PRIOR TO SHIPMENT"),
    TO_BE_AGREED("TO BE AGREED"),
    W_DOCUMENTS("W/DOCUMENTS");


    private final String name;
    PaymentTerms(final String name) {
        this.name = name;
    }
}
