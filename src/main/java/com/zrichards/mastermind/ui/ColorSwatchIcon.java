package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.PegColor;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

final class ColorSwatchIcon implements Icon {
    private final PegColor color;
    private final int size;

    ColorSwatchIcon(PegColor color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(ColorPalette.forPeg(color));
        graphics2D.fillOval(x, y, size, size);
        graphics2D.setColor(color == PegColor.WHITE ? ColorPalette.BORDER : Color.WHITE);
        graphics2D.drawOval(x, y, size - 1, size - 1);
        graphics2D.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
