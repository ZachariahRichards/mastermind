package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.PegColor;

import java.awt.Color;

final class ColorPalette {
    static final Color BACKGROUND = new Color(245, 247, 250);
    static final Color PANEL = Color.WHITE;
    static final Color BORDER = new Color(206, 212, 218);
    static final Color TEXT = new Color(33, 37, 41);
    static final Color MUTED_TEXT = new Color(88, 96, 105);
    static final Color SUCCESS = new Color(35, 134, 54);
    static final Color WARNING = new Color(214, 158, 0);
    static final Color DANGER = new Color(207, 34, 46);
    static final Color ACCENT = new Color(9, 105, 218);

    private ColorPalette() {
    }

    static Color forPeg(PegColor color) {
        return switch (color) {
            case ORANGE -> new Color(242, 140, 40);
            case YELLOW -> new Color(245, 214, 75);
            case BLUE -> new Color(45, 105, 190);
            case GREEN -> new Color(50, 150, 95);
            case RED -> new Color(210, 65, 60);
            case WHITE -> new Color(248, 249, 250);
            case PURPLE -> new Color(124, 58, 237);
            case PINK -> new Color(236, 72, 153);
            case CYAN -> new Color(6, 182, 212);
            case BROWN -> new Color(120, 72, 42);
            case BLACK -> new Color(31, 41, 55);
            case GRAY -> new Color(148, 163, 184);
            case LIME -> new Color(132, 204, 22);
            case TEAL -> new Color(20, 184, 166);
            case MAGENTA -> new Color(190, 24, 93);
        };
    }
}
