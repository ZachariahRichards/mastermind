package com.zrichards.mastermind.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraderTest {
    @Test
    void reportsResultsInGuessPositionOrder() {
        Code secret = Code.of(PegColor.BLUE, PegColor.RED, PegColor.GREEN, PegColor.ORANGE);
        Code guess = Code.of(PegColor.WHITE, PegColor.YELLOW, PegColor.RED, PegColor.ORANGE);

        assertEquals(List.of(PegResult.WRONG, PegResult.WRONG, PegResult.MISPLACED, PegResult.EXACT),
                Grader.evaluatePegs(secret, guess));
        assertEquals(new Feedback(1, 1, 4), Grader.evaluate(secret, guess));
    }

    @Test
    void duplicateGuessesDoNotOvercountOneSecretPeg() {
        Code secret = Code.of(PegColor.RED, PegColor.BLUE, PegColor.GREEN, PegColor.YELLOW);
        Code guess = Code.of(PegColor.BLUE, PegColor.BLUE, PegColor.BLUE, PegColor.BLUE);

        assertEquals(new Feedback(1, 0, 4), Grader.evaluate(secret, guess));
        assertEquals(List.of(PegResult.WRONG, PegResult.EXACT, PegResult.WRONG, PegResult.WRONG),
                Grader.evaluatePegs(secret, guess));
    }

    @Test
    void solvesVariableLengthCode() {
        Code code = Code.of(PegColor.RED, PegColor.WHITE, PegColor.ORANGE, PegColor.BLUE, PegColor.GREEN);
        Feedback feedback = Grader.evaluate(code, code);

        assertEquals(new Feedback(5, 0, 5), feedback);
        assertTrue(feedback.isSolved());
    }

    @Test
    void rejectsCodesWithDifferentLengths() {
        assertThrows(IllegalArgumentException.class, () -> Grader.evaluate(
                Code.of(PegColor.RED, PegColor.BLUE, PegColor.GREEN),
                Code.of(PegColor.RED, PegColor.BLUE, PegColor.GREEN, PegColor.WHITE)));
    }
}
