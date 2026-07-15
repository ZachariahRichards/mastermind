package com.zrichards.mastermind.ui;

import com.zrichards.mastermind.model.Code;
import com.zrichards.mastermind.model.Feedback;
import com.zrichards.mastermind.model.Grader;
import com.zrichards.mastermind.model.GuessFeedback;
import com.zrichards.mastermind.model.PegColor;
import com.zrichards.mastermind.solver.ComputerSkill;
import com.zrichards.mastermind.solver.Solver;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class MastermindFrame extends JFrame {
    private final Random random = new Random();

    private final BoardPanel playerBoard = new BoardPanel();
    private final BoardPanel solverBoard = new BoardPanel();
    private final List<GuessFeedback> playerRows = new ArrayList<>();

    private int positions = Code.LENGTH;
    private int colorCount = Code.DEFAULT_COLORS;
    private List<JComboBox<PegColor>> playerSelectors = createSelectors(positions, allowedColors());
    private List<JComboBox<PegColor>> solverSelectors = createSelectors(positions, allowedColors());
    private final JPanel playerSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    private final JPanel solverSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    private final JSpinner playerAttempts = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JSpinner solverAttempts = new JSpinner(new SpinnerNumberModel(10, 1, 30, 1));
    private final JComboBox<ComputerSkill> computerSkill = new JComboBox<>(ComputerSkill.values());
    private final JLabel playerStatus = new JLabel();
    private final JLabel solverStatus = new JLabel();
    private final JButton submitButton = new JButton("Submit Guess");

    private Code secret;

    public MastermindFrame() {
        super("Mastermind");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 640));
        setLocationByPlatform(true);
        setJMenuBar(createMenuBar());

        setContentPane(createContent());
        rebuildSelectorPanels();
        newPlayerGame();
        pack();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenu positionsMenu = new JMenu("Secret positions");
        ButtonGroup positionsGroup = new ButtonGroup();
        for (int length = Code.MIN_LENGTH; length <= Code.MAX_LENGTH; length++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(String.valueOf(length), length == positions);
            int selectedLength = length;
            item.addActionListener(event -> setPositions(selectedLength));
            positionsGroup.add(item);
            positionsMenu.add(item);
        }
        gameMenu.add(positionsMenu);

        JMenu colorsMenu = new JMenu("Allowed colors");
        ButtonGroup colorsGroup = new ButtonGroup();
        for (int count = Code.MIN_COLORS; count <= Code.MAX_COLORS; count++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(String.valueOf(count), count == colorCount);
            int selectedCount = count;
            item.addActionListener(event -> setColorCount(selectedCount));
            colorsGroup.add(item);
            colorsMenu.add(item);
        }
        gameMenu.add(colorsMenu);

        menuBar.add(gameMenu);
        return menuBar;
    }

    private void setPositions(int positions) {
        if (this.positions == positions) {
            return;
        }
        this.positions = positions;
        resetForSettingsChange();
    }

    private void setColorCount(int colorCount) {
        if (this.colorCount == colorCount) {
            return;
        }
        this.colorCount = colorCount;
        resetForSettingsChange();
    }

    private void resetForSettingsChange() {
        List<PegColor> allowedColors = allowedColors();
        playerSelectors = createSelectors(positions, allowedColors);
        solverSelectors = createSelectors(positions, allowedColors);
        rebuildSelectorPanels();
        solverBoard.setRows(List.of(), (Integer) solverAttempts.getValue(), positions);
        solverStatus.setText("Choose a " + positions + "-position secret code using "
                + colorCount + " colors and run the solver.");
        newPlayerGame();
        pack();
    }

    private void rebuildSelectorPanels() {
        populateSelectorPanel(playerSelectorPanel, playerSelectors);
        populateSelectorPanel(solverSelectorPanel, solverSelectors);
    }

    private static void populateSelectorPanel(JPanel panel, List<JComboBox<PegColor>> selectors) {
        panel.removeAll();
        panel.setOpaque(false);
        selectors.forEach(panel::add);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(ColorPalette.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Mastermind");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(ColorPalette.TEXT);
        root.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Play", createPlayerPanel());
        tabs.addTab("Solver", createSolverPanel());
        root.add(tabs, BorderLayout.CENTER);

        return root;
    }

    private JPanel createPlayerPanel() {
        JPanel panel = createPanel();
        panel.add(createPlayerControls(), BorderLayout.NORTH);
        panel.add(new JScrollPane(playerBoard), BorderLayout.CENTER);
        playerStatus.setForeground(ColorPalette.MUTED_TEXT);
        panel.add(playerStatus, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPlayerControls() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(ColorPalette.PANEL);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        controls.add(new JLabel("Guess"), constraints);
        constraints.gridx = 1;
        controls.add(playerSelectorPanel, constraints);

        constraints.gridx = 2;
        controls.add(new JLabel("Attempts"), constraints);
        constraints.gridx = 3;
        controls.add(playerAttempts, constraints);

        JButton setSecretButton = new JButton("Set Secret");
        setSecretButton.addActionListener(event -> setPlayerSecretFromHuman());
        JButton randomSecretButton = new JButton("Random Secret");
        randomSecretButton.addActionListener(event -> newPlayerGame());
        submitButton.addActionListener(event -> submitPlayerGuess());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.setOpaque(false);
        actions.add(submitButton);
        actions.add(setSecretButton);
        actions.add(randomSecretButton);

        constraints.gridx = 4;
        controls.add(actions, constraints);

        return controls;
    }

    private JPanel createSolverPanel() {
        JPanel panel = createPanel();
        panel.add(createSolverControls(), BorderLayout.NORTH);
        panel.add(new JScrollPane(solverBoard), BorderLayout.CENTER);
        solverStatus.setForeground(ColorPalette.MUTED_TEXT);
        panel.add(solverStatus, BorderLayout.SOUTH);
        solverBoard.setRows(List.of(), (Integer) solverAttempts.getValue());
        solverStatus.setText("Choose a secret code and let the solver filter possible answers.");
        return panel;
    }

    private JPanel createSolverControls() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(ColorPalette.PANEL);
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        controls.add(new JLabel("Secret"), constraints);
        constraints.gridx = 1;
        controls.add(solverSelectorPanel, constraints);

        constraints.gridx = 2;
        controls.add(new JLabel("Max"), constraints);
        constraints.gridx = 3;
        controls.add(solverAttempts, constraints);

        constraints.gridx = 4;
        controls.add(new JLabel("Skill"), constraints);
        constraints.gridx = 5;
        controls.add(computerSkill, constraints);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(event -> runSolver());
        constraints.gridx = 6;
        controls.add(solveButton, constraints);

        return controls;
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ColorPalette.BACKGROUND);
        return panel;
    }

    private void newPlayerGame() {
        secret = Code.random(random, positions, allowedColors());
        playerRows.clear();
        int maxAttempts = (Integer) playerAttempts.getValue();
        playerBoard.setRows(playerRows, maxAttempts, positions);
        submitButton.setEnabled(true);
        playerStatus.setText("New " + positions + "-position secret generated with "
                + colorCount + " colors. Attempt 1 of " + maxAttempts + ".");
    }

    private void setPlayerSecretFromHuman() {
        List<JComboBox<PegColor>> secretSelectors = createSelectors(positions, allowedColors());
        JPanel secretPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        populateSelectorPanel(secretPanel, secretSelectors);

        int result = JOptionPane.showConfirmDialog(
                this,
                secretPanel,
                "Player 1: Set Secret",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        secret = codeFrom(secretSelectors);
        playerRows.clear();
        int maxAttempts = (Integer) playerAttempts.getValue();
        playerBoard.setRows(playerRows, maxAttempts, positions);
        submitButton.setEnabled(true);
        playerStatus.setText("Player 1 secret set. Player 2: attempt 1 of " + maxAttempts + ".");

        JOptionPane.showMessageDialog(
                this,
                "Secret locked. Pass the device to Player 2.",
                "Ready for Player 2",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void submitPlayerGuess() {
        Code guess = codeFrom(playerSelectors);
        Feedback feedback = Grader.evaluate(secret, guess);
        playerRows.add(new GuessFeedback(guess, feedback, Grader.evaluatePegs(secret, guess)));

        int maxAttempts = (Integer) playerAttempts.getValue();
        playerBoard.setRows(playerRows, maxAttempts);

        if (feedback.isSolved()) {
            submitButton.setEnabled(false);
            int guesses = playerRows.size();
            playerStatus.setText("Solved in " + guesses + " guesses.");
            JOptionPane.showMessageDialog(
                    this,
                    "You cracked the code in " + guesses + " guesses!",
                    "You won!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (playerRows.size() >= maxAttempts) {
            submitButton.setEnabled(false);
            playerStatus.setText("No guesses left. Secret was " + secret.symbols() + " (" + secret + ").");
        } else {
            playerStatus.setText("Attempt " + (playerRows.size() + 1) + " of " + maxAttempts + ".");
        }
    }

    private void runSolver() {
        Code solverSecret = codeFrom(solverSelectors);
        int maxAttempts = (Integer) solverAttempts.getValue();
        ComputerSkill skill = (ComputerSkill) computerSkill.getSelectedItem();
        Solver solver = new Solver(positions, allowedColors(), skill, random);
        List<GuessFeedback> rows = solver.solve(solverSecret, maxAttempts);
        solverBoard.setRows(rows, maxAttempts);

        GuessFeedback last = rows.isEmpty() ? null : rows.get(rows.size() - 1);
        if (last != null && last.feedback().isSolved()) {
            solverStatus.setText(skill.displayName() + " computer solved "
                    + solverSecret.symbols() + " in " + rows.size() + " guesses.");
            JOptionPane.showMessageDialog(
                    this,
                    skill.displayName() + " computer cracked your code in " + rows.size() + " guesses!",
                    "Computer won!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            solverStatus.setText(skill.displayName() + " computer stopped after " + rows.size()
                    + " guesses with " + solver.candidateCount() + " possible codes remaining.");
            JOptionPane.showMessageDialog(
                    this,
                    skill.displayName() + " computer did not crack your code in " + maxAttempts
                            + " guesses.\nSecret was " + solverSecret.symbols() + " (" + solverSecret + ").",
                    "Computer lost",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private Code codeFrom(List<JComboBox<PegColor>> selectors) {
        List<PegColor> colors = new ArrayList<>(selectors.size());
        for (JComboBox<PegColor> selector : selectors) {
            colors.add((PegColor) selector.getSelectedItem());
        }
        return new Code(colors);
    }

    private List<PegColor> allowedColors() {
        return Code.allowedColors(colorCount);
    }

    private static List<JComboBox<PegColor>> createSelectors(int positions, List<PegColor> allowedColors) {
        List<JComboBox<PegColor>> selectors = new ArrayList<>(positions);
        PegColorRenderer renderer = new PegColorRenderer();
        for (int index = 0; index < positions; index++) {
            JComboBox<PegColor> selector = new JComboBox<>(allowedColors.toArray(PegColor[]::new));
            selector.setRenderer(renderer);
            selectors.add(selector);
        }
        return selectors;
    }
}
