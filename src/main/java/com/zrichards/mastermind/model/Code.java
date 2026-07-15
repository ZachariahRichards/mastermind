package com.zrichards.mastermind.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public final class Code {
    public static final int LENGTH = 4;
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 6;
    public static final int MIN_COLORS = 2;
    public static final int DEFAULT_COLORS = 6;
    public static final int MAX_COLORS = PegColor.values().length;

    private final List<PegColor> pegs;

    public Code(List<PegColor> pegs) {
        Objects.requireNonNull(pegs, "pegs");
        if (pegs.size() < MIN_LENGTH || pegs.size() > MAX_LENGTH) {
            throw new IllegalArgumentException("A Mastermind code must contain "
                    + MIN_LENGTH + " to " + MAX_LENGTH + " pegs.");
        }
        if (pegs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("A Mastermind code cannot contain null pegs.");
        }
        this.pegs = List.copyOf(pegs);
    }

    public static Code of(PegColor... pegs) {
        return new Code(Arrays.asList(pegs));
    }

    public static Code random(Random random) {
        return random(random, LENGTH);
    }

    public static Code random(Random random, int length) {
        return random(random, length, allowedColors(DEFAULT_COLORS));
    }

    public static Code random(Random random, int length, List<PegColor> allowedColors) {
        Objects.requireNonNull(random, "random");
        validateLength(length);
        validateAllowedColors(allowedColors);
        List<PegColor> generated = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            generated.add(allowedColors.get(random.nextInt(allowedColors.size())));
        }
        return new Code(generated);
    }

    public static Code parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        String compact = raw.chars()
                .filter(Character::isLetter)
                .mapToObj(character -> String.valueOf((char) character))
                .collect(Collectors.joining());
        if (compact.length() != LENGTH) {
            throw new IllegalArgumentException("Enter exactly " + LENGTH + " color symbols.");
        }

        List<PegColor> parsed = new ArrayList<>(LENGTH);
        for (int index = 0; index < compact.length(); index++) {
            parsed.add(PegColor.fromSymbol(compact.charAt(index)));
        }
        return new Code(parsed);
    }

    public static List<Code> allPossibleCodes() {
        return allPossibleCodes(LENGTH);
    }

    public static List<Code> allPossibleCodes(int length) {
        return allPossibleCodes(length, allowedColors(DEFAULT_COLORS));
    }

    public static List<Code> allPossibleCodes(int length, List<PegColor> allowedColors) {
        validateLength(length);
        validateAllowedColors(allowedColors);
        List<Code> codes = new ArrayList<>();
        buildCodes(codes, new ArrayList<>(), length, allowedColors);
        return codes;
    }

    public static List<PegColor> allowedColors(int colorCount) {
        validateColorCount(colorCount);
        return List.of(PegColor.values()).subList(0, colorCount);
    }

    private static void buildCodes(
            List<Code> codes,
            List<PegColor> pegs,
            int length,
            List<PegColor> allowedColors
    ) {
        if (pegs.size() == length) {
            codes.add(new Code(pegs));
            return;
        }
        for (PegColor color : allowedColors) {
            pegs.add(color);
            buildCodes(codes, pegs, length, allowedColors);
            pegs.remove(pegs.size() - 1);
        }
    }

    private static void validateLength(int length) {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("Code length must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH + ".");
        }
    }

    private static void validateColorCount(int colorCount) {
        if (colorCount < MIN_COLORS || colorCount > MAX_COLORS) {
            throw new IllegalArgumentException("Color count must be between "
                    + MIN_COLORS + " and " + MAX_COLORS + ".");
        }
    }

    private static void validateAllowedColors(List<PegColor> allowedColors) {
        Objects.requireNonNull(allowedColors, "allowedColors");
        validateColorCount(allowedColors.size());
        if (allowedColors.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Allowed colors cannot contain null values.");
        }
        if (allowedColors.stream().distinct().count() != allowedColors.size()) {
            throw new IllegalArgumentException("Allowed colors cannot contain duplicates.");
        }
    }

    public PegColor get(int index) {
        return pegs.get(index);
    }

    public int length() {
        return pegs.size();
    }

    public List<PegColor> pegs() {
        return pegs;
    }

    public String symbols() {
        return pegs.stream()
                .map(color -> String.valueOf(color.symbol()))
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Code code)) {
            return false;
        }
        return pegs.equals(code.pegs);
    }

    @Override
    public int hashCode() {
        return pegs.hashCode();
    }

    @Override
    public String toString() {
        return pegs.stream()
                .map(PegColor::displayName)
                .collect(Collectors.joining(", "));
    }
}
