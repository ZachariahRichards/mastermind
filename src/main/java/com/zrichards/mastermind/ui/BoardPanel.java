package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.Code;
import com.zrichards.mastermind.model.GuessFeedback;
import com.zrichards.mastermind.model.PegColor;
import com.zrichards.mastermind.model.PegResult;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

final class BoardPanel extends JPanel {
    private static final int ROW_HEIGHT = 54;
    private static final int PEG_SIZE = 28;
    private static final int LEFT_PADDING = 18;
    private static final int TOP_PADDING = 18;

    private final List<GuessFeedback> rows = new ArrayList<>();
    private int maxRows = 10;
    private int positions = Code.LENGTH;

    BoardPanel() {
        setBackground(ColorPalette.PANEL);
        setPreferredSize(new Dimension(520, 620));
    }

    void setRows(List<GuessFeedback> rows, int maxRows) {
        int rowPositions = rows.isEmpty() ? positions : rows.get(0).guess().length();
        setRows(rows, maxRows, rowPositions);
    }

    void setRows(List<GuessFeedback> rows, int maxRows, int positions) {
        this.rows.clear();
        this.rows.addAll(rows);
        this.maxRows = Math.max(1, maxRows);
        this.positions = positions;
        int preferredHeight = TOP_PADDING * 2 + this.maxRows * ROW_HEIGHT;
        int preferredWidth = Math.max(520, 48 + positions * 44 + 220);
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        g.setColor(ColorPalette.PANEL);
        g.fillRoundRect(0, 0, width - 1, getHeight() - 1, 8, 8);
        g.setColor(ColorPalette.BORDER);
        g.drawRoundRect(0, 0, width - 1, getHeight() - 1, 8, 8);

        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            int rowY = TOP_PADDING + rowIndex * ROW_HEIGHT;
            paintRowBackground(g, rowIndex, rowY, width);
            paintAttemptNumber(g, rowIndex + 1, rowY);

            if (rowIndex < rows.size()) {
                paintGuess(g, rows.get(rowIndex).guess(), rowY);
                paintFeedback(g, rows.get(rowIndex), rowY, width);
            } else {
                paintEmptyGuess(g, rowY);
            }
        }

        g.dispose();
    }

    private void paintRowBackground(Graphics2D g, int rowIndex, int rowY, int width) {
        g.setColor(rowIndex % 2 == 0 ? new Color(250, 251, 252) : Color.WHITE);
        g.fillRect(1, rowY - 6, width - 2, ROW_HEIGHT);
        g.setColor(new Color(232, 236, 240));
        g.drawLine(1, rowY + ROW_HEIGHT - 6, width - 2, rowY + ROW_HEIGHT - 6);
    }

    private void paintAttemptNumber(Graphics2D g, int attempt, int rowY) {
        g.setColor(ColorPalette.MUTED_TEXT);
        String label = String.valueOf(attempt);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(label, LEFT_PADDING - metrics.stringWidth(label) / 2, rowY + 24);
    }

    private void paintGuess(Graphics2D g, Code guess, int rowY) {
        for (int index = 0; index < guess.length(); index++) {
            paintPeg(g, guess.get(index), 48 + index * 44, rowY + 4, PEG_SIZE);
        }
    }

    private void paintEmptyGuess(Graphics2D g, int rowY) {
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(214, 220, 226));
        for (int index = 0; index < positions; index++) {
            g.drawOval(48 + index * 44, rowY + 4, PEG_SIZE, PEG_SIZE);
        }
        g.setStroke(new BasicStroke(1f));
    }

    private void paintFeedback(Graphics2D g, GuessFeedback row, int rowY, int width) {
        int x = 48 + row.guess().length() * 44 + 10;
        int y = rowY + 7;
        int exact = row.feedback().exact();
        int misplaced = row.feedback().misplaced();
        int wrong = row.guess().length() - exact - misplaced;
        paintFeedbackPips(g, row.pegResults(), x, y);
        int labelX = x + row.pegResults().size() * 14 + 10;

        g.setColor(ColorPalette.TEXT);
        g.drawString(exact + " exact", labelX, rowY + 12);
        g.setColor(ColorPalette.MUTED_TEXT);
        g.drawString(misplaced + " misplaced", labelX, rowY + 26);
        g.drawString(wrong + " wrong", labelX, rowY + 40);
    }

    private void paintFeedbackPips(Graphics2D g, List<PegResult> results, int x, int y) {
        for (int index = 0; index < results.size(); index++) {
            g.setColor(switch (results.get(index)) {
                case EXACT -> ColorPalette.SUCCESS;
                case MISPLACED -> ColorPalette.WARNING;
                case WRONG -> ColorPalette.DANGER;
            });
            int pipX = x + index * 14;
            int pipY = y;
            g.fillOval(pipX, pipY, 9, 9);
        }
    }

    private void paintPeg(Graphics2D g, PegColor color, int x, int y, int size) {
        g.setColor(ColorPalette.forPeg(color));
        g.fillOval(x, y, size, size);
        g.setColor(color == PegColor.WHITE ? ColorPalette.BORDER : new Color(255, 255, 255, 190));
        g.setStroke(new BasicStroke(1.4f));
        g.drawOval(x, y, size, size);
        g.setStroke(new BasicStroke(1f));
    }
}
