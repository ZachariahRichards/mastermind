package com.zrichards.mastermind.solver;

import com.zrichards.mastermind.model.Code;
import com.zrichards.mastermind.model.Feedback;
import com.zrichards.mastermind.model.Grader;
import com.zrichards.mastermind.model.GuessFeedback;
import com.zrichards.mastermind.model.PegColor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverTest {
    @Test
    void filtersCandidatesUsingFeedback() {
        Solver solver = new Solver(3);
        Code secret = Code.of(PegColor.RED, PegColor.GREEN, PegColor.BLUE);
        Code guess = Code.of(PegColor.RED, PegColor.RED, PegColor.RED);
        Feedback feedback = Grader.evaluate(secret, guess);

        solver.applyFeedback(guess, feedback);

        assertTrue(solver.candidateCount() < 216);
        assertTrue(solver.history().contains(new GuessFeedback(guess, feedback)));
    }

    @Test
    void easySkillDoesNotUseFeedbackToFilterCandidates() {
        Solver solver = new Solver(3, ComputerSkill.EASY, new Random(1));
        Code secret = Code.of(PegColor.RED, PegColor.GREEN, PegColor.BLUE);
        Code guess = Code.of(PegColor.RED, PegColor.RED, PegColor.RED);

        solver.applyFeedback(guess, Grader.evaluate(secret, guess));

        assertEquals(216, solver.candidateCount());
    }

    @Test
    void usesRequestedColorCountForCandidatesAndGuesses() {
        Solver solver = new Solver(3, 4, ComputerSkill.EASY, new Random(1));

        assertEquals(64, solver.candidateCount());
        assertTrue(Code.allowedColors(4).containsAll(solver.nextGuess().pegs()));
    }

    @Test
    void mediumSkillUsesFeedbackButDoesNotRunMinimax() {
        Solver solver = new Solver(3, ComputerSkill.MEDIUM, new Random(1));
        Code secret = Code.of(PegColor.RED, PegColor.GREEN, PegColor.BLUE);
        Code firstGuess = solver.nextGuess();
        Feedback feedback = Grader.evaluate(secret, firstGuess);

        solver.applyFeedback(firstGuess, feedback);
        Code nextGuess = solver.nextGuess();

        assertTrue(solver.candidateCount() < 216);
        assertEquals(secret.length(), nextGuess.length());
    }

    @Test
    void solvesRepresentativeThreeAndFourPositionCodes() {
        assertSolved(Code.of(PegColor.WHITE, PegColor.RED, PegColor.BLUE), 8);
        assertSolved(Code.of(PegColor.GREEN, PegColor.YELLOW, PegColor.GREEN, PegColor.ORANGE), 8);
    }

    private static void assertSolved(Code secret, int maxAttempts) {
        Solver solver = new Solver(secret.length());
        List<GuessFeedback> guesses = solver.solve(secret, maxAttempts);

        assertTrue(guesses.size() <= maxAttempts);
        assertTrue(guesses.get(guesses.size() - 1).feedback().isSolved());
        assertEquals(secret, guesses.get(guesses.size() - 1).guess());
    }
}
