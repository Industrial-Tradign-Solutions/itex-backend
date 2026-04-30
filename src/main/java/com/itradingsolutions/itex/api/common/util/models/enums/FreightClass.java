package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum FreightClass implements BaseEnum {

    F50("50", "Durable freight that fits on a standard 4' × 4' pallet", "50+ lbs"),
    F55("55", "Bricks, cement, hardwood flooring, construction materials", "35–50 lbs"),
    F60("60", "Car accessories, car parts", "30–35 lbs"),
    F65("65", "Car accessories and parts, boxed books, bottled drinks", "22.5–30 lbs"),
    F70("70", "Car accessories and parts, auto engines, food items", "15–22.5 lbs"),
    F77_5("77.5", "Tires, bathroom fixtures", "13.5–15 lbs"),
    F85("85", "Crated machinery, cast iron stoves", "12–13.5 lbs"),
    F92_5("92.5", "Computers, monitors, refrigerators", "10.5–12 lbs"),
    F100("100", "Car covers, canvas, boat covers, wine cases, caskets", "9–10.5 lbs"),
    F110("110", "Cabinets, framed art, table saws", "8–9 lbs"),
    F125("125", "Small home appliances", "7–8 lbs"),
    F150("150", "Auto sheet metal, bookcases", "6–7 lbs"),
    F175("175", "Clothing, couches, stuffed furniture", "5–6 lbs"),
    F200("200", "Sheet metal parts, aluminum tables, packaged mattresses, aircraft parts", "4–5 lbs"),
    F250("250", "Mattresses and box springs, plasma TVs, bamboo furniture", "3–4 lbs"),
    F300("300", "Model boats, assembled chairs, tables, wood cabinets", "2–3 lbs"),
    F400("400", "Deer antlers", "1–2 lbs"),
    F500("500", "Gold dust, ping pong balls", "<1 lb"),
    ;

    private final String name;
    private final String type;
    private final String weight ;


    FreightClass(final String name, final String type, final String weight) {
        this.name = name;
        this.type = type;
        this.weight = weight;
    }
}
