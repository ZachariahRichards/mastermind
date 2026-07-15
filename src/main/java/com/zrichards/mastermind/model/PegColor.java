package com.zrichards.mastermind.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum PegColor {
    ORANGE('O', "Orange"),
    YELLOW('Y', "Yellow"),
    BLUE('B', "Blue"),
    GREEN('G', "Green"),
    RED('R', "Red"),
    WHITE('W', "White"),
    PURPLE('P', "Purple"),
    PINK('K', "Pink"),
    CYAN('C', "Cyan"),
    BROWN('N', "Brown"),
    BLACK('D', "Black"),
    GRAY('A', "Gray"),
    LIME('L', "Lime"),
    TEAL('T', "Teal"),
    MAGENTA('M', "Magenta");

    private final char symbol;
    private final String displayName;

    PegColor(char symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }

    public char symbol() {
        return symbol;
    }

    public String displayName() {
        return displayName;
    }

    public static PegColor fromSymbol(char symbol) {
        char upper = Character.toUpperCase(symbol);
        for (PegColor color : values()) {
            if (color.symbol == upper) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color symbol: " + symbol);
    }

    public static String validSymbols() {
        return Arrays.stream(values())
                .map(color -> String.valueOf(color.symbol))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return displayName;
    }
}
