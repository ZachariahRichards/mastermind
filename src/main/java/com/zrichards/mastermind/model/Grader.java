package com.zrichards.mastermind.model;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Grader {
    private Grader() {
    }

    public static Feedback evaluate(Code secret, Code guess) {
        List<PegResult> results = evaluatePegs(secret, guess);
        int exact = (int) results.stream().filter(result -> result == PegResult.EXACT).count();
        int misplaced = (int) results.stream().filter(result -> result == PegResult.MISPLACED).count();
        return new Feedback(exact, misplaced, secret.length());
    }

    public static List<PegResult> evaluatePegs(Code secret, Code guess) {
        Objects.requireNonNull(secret, "secret");
        Objects.requireNonNull(guess, "guess");
        if (secret.length() != guess.length()) {
            throw new IllegalArgumentException("Secret and guess must have the same length.");
        }

        Map<PegColor, Integer> remainingSecret = new EnumMap<>(PegColor.class);
        List<PegResult> results = new ArrayList<>(secret.length());

        for (int index = 0; index < secret.length(); index++) {
            PegColor secretPeg = secret.get(index);
            PegColor guessPeg = guess.get(index);
            if (secretPeg == guessPeg) {
                results.add(PegResult.EXACT);
            } else {
                results.add(null);
                remainingSecret.merge(secretPeg, 1, Integer::sum);
            }
        }

        for (int index = 0; index < secret.length(); index++) {
            if (results.get(index) == PegResult.EXACT) {
                continue;
            }
            PegColor guessPeg = guess.get(index);
            int available = remainingSecret.getOrDefault(guessPeg, 0);
            if (available > 0) {
                results.set(index, PegResult.MISPLACED);
                remainingSecret.put(guessPeg, available - 1);
            } else {
                results.set(index, PegResult.WRONG);
            }
        }

        return List.copyOf(results);
    }
}
