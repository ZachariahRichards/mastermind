# Mastermind

This is a rebuilt Maven version of the original `mastermind-artifacts` Java project. It keeps the classic Mastermind rules, adds a Swing UI, and separates the game rules from presentation code.

## Improvements

- Standard Maven project layout.
- Java 17 package structure and class naming.
- Immutable `Code` and `Feedback` model objects.
- Duplicate-safe grading logic.
- Constraint-based solver that filters all possible codes by feedback.
- Knuth-style minimax guess selection after the opening guess.
- Swing UI with a playable game tab and solver tab.
- No external runtime dependencies.

## Run

Install JDK 21 and Maven, then run:

```bash
mvn clean compile exec:java
```

To build a jar:

```bash
mvn clean package
java -jar target/mastermind-1.0.0.jar
```

The build enforces JDK 21 and compiles with `--release 21`.

## Test

Run the automated test suite with:

```bash
mvn test
```

## Rules

A code can contain three to six pegs and defaults to four. The available colors are:

- `O` Orange
- `Y` Yellow
- `B` Blue
- `G` Green
- `R` Red
- `W` White

Feedback reports exact matches and misplaced color matches. Exact means the color and position are correct. Misplaced means the color appears in the secret code but in a different position.
