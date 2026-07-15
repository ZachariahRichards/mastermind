package com.zrichards.mastermind.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackTest {
    @Test
    void validatesCountsAgainstPositionCount() {
        assertThrows(IllegalArgumentException.class, () -> new Feedback(-1, 0, 4));
        assertThrows(IllegalArgumentException.class, () -> new Feedback(0, -1, 4));
        assertThrows(IllegalArgumentException.class, () -> new Feedback(3, 2, 4));
        assertFalse(new Feedback(3, 0, 4).isSolved());
    }

    @Test
    void guessFeedbackDefensivelyCopiesPositionalResults() {
        Code guess = Code.of(PegColor.RED, PegColor.BLUE, PegColor.GREEN);
        List<PegResult> results = new java.util.ArrayList<>(
                List.of(PegResult.EXACT, PegResult.WRONG, PegResult.MISPLACED));
        GuessFeedback row = new GuessFeedback(guess, new Feedback(1, 1, 3), results);
        results.set(0, PegResult.WRONG);

        assertEquals(PegResult.EXACT, row.pegResults().get(0));
        assertThrows(IllegalArgumentException.class,
                () -> new GuessFeedback(guess, new Feedback(1, 0, 3), List.of(PegResult.EXACT)));
    }
}
