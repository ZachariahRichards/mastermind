package com.zrichards.mastermind.solver;

import com.zrichards.mastermind.model.Code;
import com.zrichards.mastermind.model.Feedback;
import com.zrichards.mastermind.model.Grader;
import com.zrichards.mastermind.model.GuessFeedback;
import com.zrichards.mastermind.model.PegColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public final class Solver {
    private final ComputerSkill skill;
    private final Random random;
    private final List<PegColor> allowedColors;
    private final List<Code> allCodes;
    private final List<GuessFeedback> history = new ArrayList<>();
    private List<Code> candidates;

    public Solver() {
        this(Code.LENGTH);
    }

    public Solver(int positions) {
        this(positions, ComputerSkill.HARD);
    }

    public Solver(int positions, ComputerSkill skill) {
        this(positions, skill, new Random());
    }

    public Solver(int positions, ComputerSkill skill, Random random) {
        this(positions, Code.allowedColors(Code.DEFAULT_COLORS), skill, random);
    }

    public Solver(int positions, int colorCount, ComputerSkill skill, Random random) {
        this(positions, Code.allowedColors(colorCount), skill, random);
    }

    public Solver(int positions, List<PegColor> allowedColors, ComputerSkill skill, Random random) {
        this.skill = Objects.requireNonNull(skill, "skill");
        this.random = Objects.requireNonNull(random, "random");
        this.allowedColors = List.copyOf(allowedColors);
        allCodes = Code.allPossibleCodes(positions, this.allowedColors);
        candidates = new ArrayList<>(allCodes);
    }

    public Code nextGuess() {
        if (skill == ComputerSkill.EASY) {
            return Code.random(random, allCodes.get(0).length(), allowedColors);
        }
        if (history.isEmpty()) {
            return openingGuess();
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No possible codes match the feedback history.");
        }
        if (skill == ComputerSkill.MEDIUM || candidates.size() == 1) {
            return candidates.get(0);
        }
        if (allCodes.size() > 2_000) {
            return candidates.get(0);
        }
        return chooseMinimaxGuess();
    }

    public void applyFeedback(Code guess, Feedback feedback) {
        history.add(new GuessFeedback(guess, feedback));
        if (skill == ComputerSkill.EASY) {
            return;
        }
        candidates = candidates.stream()
                .filter(candidate -> Grader.evaluate(candidate, guess).equals(feedback))
                .toList();
    }

    public int candidateCount() {
        return candidates.size();
    }

    public List<GuessFeedback> history() {
        return List.copyOf(history);
    }

    public List<GuessFeedback> solve(Code secret, int maxAttempts) {
        List<GuessFeedback> guesses = new ArrayList<>();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Code guess = nextGuess();
            Feedback feedback = Grader.evaluate(secret, guess);
            applyFeedback(guess, feedback);
            GuessFeedback row = new GuessFeedback(guess, feedback, Grader.evaluatePegs(secret, guess));
            guesses.add(row);
            if (feedback.isSolved()) {
                break;
            }
        }
        return guesses;
    }

    private Code openingGuess() {
        List<PegColor> opening = new ArrayList<>();
        int length = candidates.get(0).length();
        for (int index = 0; index < length; index++) {
            opening.add(index < length / 2 ? allowedColors.get(0) : allowedColors.get(1));
        }
        return new Code(opening);
    }

    private Code chooseMinimaxGuess() {
        Set<Code> candidateSet = new HashSet<>(candidates);
        Code bestGuess = null;
        int bestWorstPartition = Integer.MAX_VALUE;
        boolean bestGuessIsCandidate = false;

        for (Code possibleGuess : allCodes) {
            int worstPartition = worstPartitionSize(possibleGuess);
            boolean possibleGuessIsCandidate = candidateSet.contains(possibleGuess);

            if (bestGuess == null
                    || worstPartition < bestWorstPartition
                    || (worstPartition == bestWorstPartition && possibleGuessIsCandidate && !bestGuessIsCandidate)) {
                bestGuess = possibleGuess;
                bestWorstPartition = worstPartition;
                bestGuessIsCandidate = possibleGuessIsCandidate;
            }
        }

        return bestGuess;
    }

    private int worstPartitionSize(Code possibleGuess) {
        Map<Feedback, Integer> partitions = new HashMap<>();
        int worst = 0;

        for (Code possibleSecret : candidates) {
            Feedback feedback = Grader.evaluate(possibleSecret, possibleGuess);
            int count = partitions.merge(feedback, 1, Integer::sum);
            worst = Math.max(worst, count);
        }

        return worst;
    }
}
