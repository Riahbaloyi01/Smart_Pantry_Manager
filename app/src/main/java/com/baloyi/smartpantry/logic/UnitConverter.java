package com.baloyi.smartpantry.logic;

public class UnitConverter {

    public static final String AMOUNT = "amount"; // g, kg, ml, l, tsp, tbsp
    public static final String COUNT = "count";   // pcs

    // Says whether a unit measures an amount or counts pieces
    public static String getType(String unit) {
        if (unit == null) {
            return "";
        }
        switch (unit.trim().toLowerCase()) {
            case "g":
            case "kg":
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
                return AMOUNT;
            case "pcs":
                return COUNT;
            default:
                return "";
        }
    }

    // Converts to a base unit: grams/millilitres (we treat 1 g as roughly 1 ml) or pieces
    public static double toBase(double quantity, String unit) {
        switch (unit.trim().toLowerCase()) {
            case "kg":
            case "l":
                return quantity * 1000;
            case "tbsp":
                return quantity * 15;
            case "tsp":
                return quantity * 5;
            default:
                return quantity; // g, ml and pcs are already base units
        }
    }
}