package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.PegColor;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

final class PegColorRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof PegColor color) {
            setText(color.displayName() + " (" + color.symbol() + ")");
            setIcon(new ColorSwatchIcon(color, 14));
        }
        return this;
    }
}
