package com.zrichards.mastermind.model;

public record Feedback(int exact, int misplaced, int positions) {
    public Feedback(int exact, int misplaced) {
        this(exact, misplaced, Code.LENGTH);
    }

    public Feedback {
        if (exact < 0 || misplaced < 0) {
            throw new IllegalArgumentException("Feedback values cannot be negative.");
        }
        if (exact + misplaced > positions) {
            throw new IllegalArgumentException("Feedback cannot exceed code length.");
        }
    }

    public boolean isSolved() {
        return exact == positions;
    }

    @Override
    public String toString() {
        return exact + " exact, " + misplaced + " misplaced";
    }
}
