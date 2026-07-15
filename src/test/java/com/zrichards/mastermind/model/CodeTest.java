package com.zrichards.mastermind.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeTest {
    @Test
    void acceptsEverySupportedLength() {
        for (int length = Code.MIN_LENGTH; length <= Code.MAX_LENGTH; length++) {
            Code code = new Code(new ArrayList<>(java.util.Collections.nCopies(length, PegColor.BLUE)));
            assertEquals(length, code.length());
        }
    }

    @Test
    void rejectsUnsupportedLengthsAndNullPegs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Code(List.of(PegColor.BLUE, PegColor.RED)));
        assertThrows(IllegalArgumentException.class,
                () -> new Code(java.util.Collections.nCopies(Code.MAX_LENGTH + 1, PegColor.BLUE)));

        List<PegColor> withNull = new ArrayList<>(List.of(PegColor.BLUE, PegColor.RED, PegColor.GREEN));
        withNull.set(1, null);
        assertThrows(IllegalArgumentException.class, () -> new Code(withNull));
    }

    @Test
    void defensivelyCopiesPegs() {
        List<PegColor> pegs = new ArrayList<>(List.of(PegColor.BLUE, PegColor.RED, PegColor.GREEN));
        Code code = new Code(pegs);
        pegs.set(0, PegColor.WHITE);

        assertEquals(PegColor.BLUE, code.get(0));
        assertThrows(UnsupportedOperationException.class, () -> code.pegs().set(0, PegColor.WHITE));
    }

    @Test
    void randomCodeUsesRequestedLengthAndIsDeterministicForSeed() {
        Code first = Code.random(new Random(42), 6);
        Code second = Code.random(new Random(42), 6);

        assertEquals(6, first.length());
        assertEquals(first, second);
        assertThrows(IllegalArgumentException.class, () -> Code.random(new Random(), 2));
    }

    @Test
    void randomCodeUsesOnlyAllowedColors() {
        List<PegColor> allowedColors = Code.allowedColors(3);

        Code code = Code.random(new Random(42), 6, allowedColors);

        assertTrue(allowedColors.containsAll(code.pegs()));
    }

    @Test
    void generatesEveryPossibleCodeForLengthThree() {
        List<Code> codes = Code.allPossibleCodes(3);

        assertEquals(216, codes.size());
        assertEquals(216, codes.stream().distinct().count());
        assertNotEquals(codes.get(0), codes.get(codes.size() - 1));
    }

    @Test
    void generatesEveryPossibleCodeForRequestedColors() {
        List<Code> codes = Code.allPossibleCodes(3, Code.allowedColors(4));

        assertEquals(64, codes.size());
        assertEquals(64, codes.stream().distinct().count());
        assertTrue(codes.stream()
                .flatMap(code -> code.pegs().stream())
                .allMatch(Code.allowedColors(4)::contains));
    }

    @Test
    void supportsFifteenAllowedColors() {
        List<PegColor> colors = Code.allowedColors(15);
        List<Code> codes = Code.allPossibleCodes(3, colors);

        assertEquals(15, colors.size());
        assertEquals(3_375, codes.size());
        assertTrue(colors.contains(PegColor.MAGENTA));
    }

    @Test
    void rejectsUnsupportedColorCounts() {
        assertThrows(IllegalArgumentException.class, () -> Code.allowedColors(1));
        assertThrows(IllegalArgumentException.class, () -> Code.allowedColors(Code.MAX_COLORS + 1));
        assertThrows(IllegalArgumentException.class,
                () -> Code.allPossibleCodes(3, List.of(PegColor.BLUE, PegColor.BLUE)));
    }

    @Test
    void parsesAndFormatsDefaultLengthCode() {
        Code code = Code.parse("O-Y-B-G");

        assertEquals("OYBG", code.symbols());
        assertEquals("Orange, Yellow, Blue, Green", code.toString());
    }
}
