package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.Code;
import com.zrichards.mastermind.model.Feedback;
import com.zrichards.mastermind.model.GuessFeedback;
import com.zrichards.mastermind.model.PegColor;
import com.zrichards.mastermind.model.PegResult;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardPanelTest {
    @Test
    void rendersScoringPinsInOnePositionAlignedRow() {
        BoardPanel board = new BoardPanel();
        Code guess = Code.of(PegColor.WHITE, PegColor.WHITE, PegColor.WHITE, PegColor.ORANGE);
        GuessFeedback row = new GuessFeedback(guess, new Feedback(1, 0, 4),
                List.of(PegResult.WRONG, PegResult.WRONG, PegResult.WRONG, PegResult.EXACT));
        board.setRows(List.of(row), 10, 4);
        board.setSize(board.getPreferredSize());
        BufferedImage image = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_RGB);

        board.paint(image.getGraphics());

        int scoreX = 48 + 4 * 44 + 10;
        assertEquals(ColorPalette.DANGER.getRGB(), image.getRGB(scoreX + 4, 18 + 7 + 4));
        assertEquals(ColorPalette.SUCCESS.getRGB(), image.getRGB(scoreX + 3 * 14 + 4, 18 + 7 + 4));
    }

    @Test
    void widensForSixPositions() {
        BoardPanel board = new BoardPanel();
        board.setRows(List.of(), 10, 6);

        assertTrue(board.getPreferredSize().width >= 532);
    }

    @Test
    void leavesSpaceBetweenSixScoringPinsAndLabels() {
        int scoreX = 48 + 6 * 44 + 10;
        int lastPinRightEdge = scoreX + 5 * 14 + 9;
        int labelX = scoreX + 6 * 14 + 10;

        assertTrue(labelX > lastPinRightEdge);
    }
}
