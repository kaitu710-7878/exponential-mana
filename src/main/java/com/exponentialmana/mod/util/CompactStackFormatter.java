package com.exponentialmana.mod.util;

import java.text.DecimalFormat;

/**
 * Utility for formatting large item stack quantities.
 * e.g., 1,500 -> 1.5K, 20,000,000 -> 20M, 2,147,483,647 -> 2.1B
 */
public class CompactStackFormatter {
    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

    public static String formatCount(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 1_000_000) {
            double k = count / 1000.0;
            return ONE_DECIMAL.format(k) + "K";
        } else if (count < 1_000_000_000) {
            double m = count / 1_000_000.0;
            return ONE_DECIMAL.format(m) + "M";
        } else {
            double b = count / 1_000_000_000.0;
            return ONE_DECIMAL.format(b) + "B";
        }
    }
}
