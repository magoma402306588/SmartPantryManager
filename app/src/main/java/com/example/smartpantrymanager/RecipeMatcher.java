package com.example.smartpantrymanager;

import java.util.Map;

public class RecipeMatcher {

    public static String normalize(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase();
        if (clean.endsWith("es") && clean.length() > 3) {
            return clean.substring(0, clean.length() - 2);
        } else if (clean.endsWith("s") && !clean.endsWith("ss") && clean.length() > 2) {
            return clean.substring(0, clean.length() - 1);
        }
        return clean;
    }

    public static boolean isMatch(String ingredientsRaw, Map<String, Double> pantryMap) {
        if (ingredientsRaw == null || ingredientsRaw.isEmpty()) return false;

        String[] requiredItems = ingredientsRaw.split(";");
        for (String item : requiredItems) {
            String[] parts = item.split(":");
            if (parts.length < 2) continue;

            String reqNameNorm = normalize(parts[0]);
            double reqQty;
            try {
                reqQty = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
                return false;
            }

            Double inStockQty = pantryMap.get(reqNameNorm);
            if (inStockQty == null || inStockQty < reqQty) {
                return false;
            }
        }
        return true;
    }
}