package com.zrichards.mastermind.model;

import java.util.ArrayList;
import java.util.List;

public record GuessFeedback(Code guess, Feedback feedback, List<PegResult> pegResults) {
    public GuessFeedback {
        pegResults = List.copyOf(pegResults);
        if (pegResults.size() != guess.length()) {
            throw new IllegalArgumentException("A result is required for every guessed peg.");
        }
    }

    public GuessFeedback(Code guess, Feedback feedback) {
        this(guess, feedback, groupedResults(feedback));
    }

    private static List<PegResult> groupedResults(Feedback feedback) {
        List<PegResult> results = new ArrayList<>(feedback.positions());
        results.addAll(java.util.Collections.nCopies(feedback.exact(), PegResult.EXACT));
        results.addAll(java.util.Collections.nCopies(feedback.misplaced(), PegResult.MISPLACED));
        results.addAll(java.util.Collections.nCopies(
                feedback.positions() - feedback.exact() - feedback.misplaced(), PegResult.WRONG));
        return results;
    }
}
