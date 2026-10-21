package com.baloyi.smartpantry.logic;

public class IngredientNormalizer {

    // Turns an ingredient name into a simple, comparable form
    public static String normalize(String name) {
        if (name == null) {
            return "";
        }
        String n = name.trim().toLowerCase().replaceAll("\\s+", " ");

        // berries -> berry
        if (n.length() > 4 && n.endsWith("ies")) {
            return n.substring(0, n.length() - 3) + "y";
        }
        // tomatoes -> tomato, potatoes -> potato
        if (n.length() > 4 && n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);
        }
        // peaches -> peach, radishes -> radish, boxes -> box
        if (n.length() > 4 && (n.endsWith("ches") || n.endsWith("shes") || n.endsWith("xes"))) {
            return n.substring(0, n.length() - 2);
        }
        // eggs -> egg, beans -> bean (but not "grass" or "hummus")
        if (n.length() > 3 && n.endsWith("s") && !n.endsWith("ss") && !n.endsWith("us")) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }
}