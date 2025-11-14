import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NumberGuessingGameFX.java
 *
 * Single-file JavaFX number guessing game with:
 * - Difficulty levels (Easy/Medium/Hard/Custom)
 * - Hints: Odd/Even, Narrow-range, 50/50 (splits the current range and returns the correct half)
 * - Attempts, rounds, scoring
 * - Session stats and persistent leaderboard saved under the user's home folder
 *
 * Run with JavaFX modules on module-path.
 */
public class NumberGuessingGameFX extends Application {
    private static final Random RAND = new Random();

    // Leaderboard location (user home folder)
    private static final Path DEFAULT_LEADERBOARD_DIR =
            Paths.get(System.getProperty("user.home"), "NumberGuessingGame");
    private static final Path DEFAULT_LEADERBOARD_FILE = DEFAULT_LEADERBOARD_DIR.resolve("leaderboard.txt");

    // Session stats
    private int totalScore = 0;
    private int roundsPlayed = 0;
    private int totalWins = 0;
    private int bestRoundScore = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;

    // UI controls referenced across methods
    private Stage primaryStage;
    private String playerName = "Player";

    // Round state
    private int secret;
    private int low, high;
    private int maxNum, maxAttempts;
    private int attemptsLeft;
    private int attemptsUsed;
    private boolean usedOddEvenHint, usedNarrowHint, used5050Hint;
    private List<Integer> possibleNumbers;

    // UI nodes for round
    private Label rangeLabel = new Label();
    private Label attemptsLabel = new Label();
    private Label feedbackLabel = new Label();
    private TextField guessField = new TextField();
    private Button guessButton = new Button("Guess");
    private Button oddEvenBtn = new Button("Odd/Even Hint");
    private Button narrowBtn = new Button("Narrow-range Hint");
    private Button fiftyBtn = new Button("50/50 Hint");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Number Guessing Game");

        showNameScene();
    }

    // Initial name scene (title is "Number Guessing Game")
    private void showNameScene() {
        Label title = new Label("Number Guessing Game");
        title.setFont(Font.font(28));

        Label ask = new Label("Enter your name:");
        ask.setFont(Font.font(16));
        TextField nameField = new TextField();
        nameField.setPromptText("Your name");

        Button continueBtn = new Button("Continue");
        continueBtn.setDefaultButton(true);

        continueBtn.setOnAction(e -> {
            String n = nameField.getText();
            if (n == null || n.trim().isEmpty()) {
                boolean useDefault = askConfirm("No name entered. Use default 'Player'?");
                if (!useDefault) return;
                playerName = "Player";
            } else playerName = n.trim();
            showMainMenu();
        });

        nameField.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) continueBtn.fire();
        });

        VBox v = new VBox(12, title, ask, nameField, continueBtn);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(30));
        v.setPrefWidth(520);

        Scene scene = new Scene(v);
        applyGlobalStyling(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainMenu() {
        Label hi = new Label("Hello, " + playerName + "!");
        hi.setFont(Font.font(20));

        Label choose = new Label("Choose difficulty:");
        choose.setFont(Font.font(16));

        Button easy = new Button("Easy (1-50, 10 attempts)");
        Button medium = new Button("Medium (1-100, 7 attempts)");
        Button hard = new Button("Hard (1-200, 5 attempts)");
        Button custom = new Button("Custom");

        HBox row = new HBox(10, easy, medium, hard, custom);
        row.setAlignment(Pos.CENTER);

        Label stats = new Label(sessionStatsText());
        stats.setFont(Font.font(14));

        Button showLeader = new Button("Show Leaderboard");
        Button quit = new Button("Quit");

        easy.setOnAction(e -> startNewRound(50, 10));
        medium.setOnAction(e -> startNewRound(100, 7));
        hard.setOnAction(e -> startNewRound(200, 5));
        custom.setOnAction(e -> showCustomDialog());

        showLeader.setOnAction(e -> showLeaderboardDialog());
        quit.setOnAction(e -> {
            boolean sure = askConfirm("Are you sure you want to quit?");
            if (sure) Platform.exit();
        });

        VBox root = new VBox(12, hi, choose, row, new Separator(), stats, showLeader, quit);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setPrefWidth(700);

        Scene scene = new Scene(root);
        applyGlobalStyling(scene);
        primaryStage.setScene(scene);
    }

    private void showCustomDialog() {
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Custom Difficulty");
        dialog.setHeaderText("Enter maximum number and attempts");

        ButtonType startBtnType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField maxField = new TextField("100");
        TextField attemptsField = new TextField("10");

        grid.add(new Label("Maximum number (>=10):"), 0, 0);
        grid.add(maxField, 1, 0);
        grid.add(new Label("Attempts (>=1):"), 0, 1);
        grid.add(attemptsField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == startBtnType) {
                try {
                    int max = Integer.parseInt(maxField.getText().trim());
                    int at = Integer.parseInt(attemptsField.getText().trim());
                    if (max < 10 || at < 1) return null;
                    return new Pair<>(max, at);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return null;
        });

        Optional<Pair<Integer, Integer>> res = dialog.showAndWait();
        res.ifPresent(p -> startNewRound(p.getKey(), p.getValue()));
    }

    private void startNewRound(int maxNum, int attempts) {
        this.maxNum = maxNum;
        this.maxAttempts = attempts;
        this.secret = RAND.nextInt(maxNum) + 1;
        this.low = 1;
        this.high = maxNum;
        this.attemptsLeft = attempts;
        this.attemptsUsed = 0;
        this.usedOddEvenHint = false;
        this.usedNarrowHint = false;
        this.used5050Hint = false;

        // initialize full pool 1..maxNum
        this.possibleNumbers = new ArrayList<>();
        for (int i = 1; i <= maxNum; i++) possibleNumbers.add(i);

        showRoundScene();
    }

    private void showRoundScene() {
        Label header = new Label("Guess the number!");
        header.setFont(Font.font(22));

        rangeLabel.setFont(Font.font(16));
        attemptsLabel.setFont(Font.font(16));
        updateRoundLabels();

        feedbackLabel.setFont(Font.font(15));
        feedbackLabel.setWrapText(true);

        guessField.setPromptText("Enter number");
        guessField.setFont(Font.font(16));
        guessField.setPrefWidth(160);

        guessButton.setFont(Font.font(16));
        guessButton.setOnAction(e -> handleGuess());
        guessField.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) handleGuess();
        });

        oddEvenBtn.setOnAction(e -> applyOddEvenHint());
        narrowBtn.setOnAction(e -> applyNarrowHint());
        fiftyBtn.setOnAction(e -> apply5050Hint());

        HBox inputRow = new HBox(10, guessField, guessButton);
        inputRow.setAlignment(Pos.CENTER);

        HBox hintRow = new HBox(10, oddEvenBtn, narrowBtn, fiftyBtn);
        hintRow.setAlignment(Pos.CENTER);

        Button giveUp = new Button("Give Up");
        giveUp.setOnAction(e -> {
            boolean confirm = askConfirm("Give up this round?");
            if (confirm) endRound(false);
        });

        Button backToMenu = new Button("Back to Menu");
        backToMenu.setOnAction(e -> {
            boolean confirm = askConfirm("Abort this round and return to menu? You will forfeit this round.");
            if (confirm) {
                endRound(false);
                showMainMenu();
            }
        });

        HBox bottomRow = new HBox(10, giveUp, backToMenu);
        bottomRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(12, header, rangeLabel, attemptsLabel, inputRow, hintRow, feedbackLabel, bottomRow);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setPrefWidth(760);

        Scene scene = new Scene(layout);
        applyGlobalStyling(scene);
        primaryStage.setScene(scene);

        updateHintButtons();
        feedbackLabel.setText("Good luck!");
    }

    private void updateRoundLabels() {
        rangeLabel.setText(String.format("Range: %d to %d", low, high));
        attemptsLabel.setText(String.format("Attempts left: %d", attemptsLeft));
    }

    // prune the possibleNumbers pool to the current [low, high] bounds
    private void prunePossibleNumbers() {
        if (possibleNumbers == null) return;
        possibleNumbers = possibleNumbers.stream()
                .filter(n -> n >= low && n <= high)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private void handleGuess() {
        String text = guessField.getText();
        Integer g = tryParseInt(text);
        if (g == null) {
            feedbackLabel.setText("Enter a valid integer.");
            return;
        }

        // ensure guess is inside current low-high
        if (g < low || g > high) {
            feedbackLabel.setText("Your guess is outside the allowed range (" + low + " to " + high + ").");
            return;
        }

        // if we maintain a possibleNumbers pool, ensure guess is in it (helps after 50/50 or prunes)
        if (used5050Hint && (possibleNumbers == null || !possibleNumbers.contains(g))) {
            feedbackLabel.setText("That number was removed by the 50/50 hint. Choose from the remaining pool.");
            if (possibleNumbers != null && possibleNumbers.size() <= 20) {
                String list = possibleNumbers.stream().map(String::valueOf).collect(Collectors.joining(", "));
                showInfo("Remaining numbers: " + list);
            }
            return;
        }

        attemptsLeft--;
        attemptsUsed++;

        if (g == secret) {
            int score = computeScore(maxNum, maxAttempts, attemptsUsed);
            totalScore += score;
            roundsPlayed++;
            totalWins++;
            currentStreak++;
            bestStreak = Math.max(bestStreak, currentStreak);
            bestRoundScore = Math.max(bestRoundScore, score);

            showInfo(String.format("Correct! Number: %d\nAttempts: %d\nScore this round: %d", secret, attemptsUsed, score));
            // Offer to save score
            saveScoreToLeaderboard(score);
            endRound(true);
            showMainMenu();
            return;
        } else if (g < secret) {
            feedbackLabel.setText("Higher than " + g + " — keep trying!");
            // tighten low bound
            low = Math.max(low, g + 1);
        } else {
            feedbackLabel.setText("Lower than " + g + " — keep trying!");
            // tighten high bound
            high = Math.min(high, g - 1);
        }

        // Keep the possibleNumbers pool in sync with the new bounds
        prunePossibleNumbers();

        updateRoundLabels();
        updateHintButtons();

        if (attemptsLeft <= 0) {
            showInfo("Out of attempts! The number was " + secret);
            endRound(false);
            showMainMenu();
        }
        guessField.clear();
    }

    private void applyOddEvenHint() {
        if (usedOddEvenHint) { feedbackLabel.setText("Odd/Even hint already used."); return; }
        usedOddEvenHint = true;
        String oe = (secret % 2 == 0) ? "Even" : "Odd";
        feedbackLabel.setText("Odd/Even hint: The number is " + oe);
        updateHintButtons();
    }

    private void applyNarrowHint() {
        if (usedNarrowHint) { feedbackLabel.setText("Narrow-range hint already used."); return; }
        usedNarrowHint = true;
        int delta = Math.max(1, maxNum / 10);
        int newLow = Math.max(low, secret - delta);
        int newHigh = Math.min(high, secret + delta);
        low = newLow;
        high = newHigh;

        // prune the pool to the new bounds
        prunePossibleNumbers();

        feedbackLabel.setText(String.format("Narrow hint: number between %d and %d", newLow, newHigh));
        updateRoundLabels();
        updateHintButtons();

        if (possibleNumbers.size() <= 12) {
            String list = possibleNumbers.stream().map(String::valueOf).collect(Collectors.joining(", "));
            showInfo("Remaining numbers: " + list);
        }
    }

    /**
     * New 50/50 behavior:
     * - Split the current [low, high] into two contiguous halves
     * - Determine which half contains the secret
     * - Update low/high to be that half and prune the pool
     * This makes the "50/50" intuitive and effective.
     */
    private void apply5050Hint() {
        if (used5050Hint) { feedbackLabel.setText("50/50 hint already used."); return; }
        used5050Hint = true;

        // compute split
        int length = high - low + 1;
        if (length <= 2) {
            // if only 1 or 2 numbers remain, just reveal bounds (no meaningful split)
            feedbackLabel.setText("Too few numbers to split further.");
            prunePossibleNumbers();
            updateHintButtons();
            updateRoundLabels();
            return;
        }

        int mid = low + (length / 2) - 1; // end of lower half
        int lowHalfStart = low;
        int lowHalfEnd = mid;
        int highHalfStart = mid + 1;
        int highHalfEnd = high;

        boolean secretInLowHalf = (secret >= lowHalfStart && secret <= lowHalfEnd);

        if (secretInLowHalf) {
            low = lowHalfStart;
            high = lowHalfEnd;
            feedbackLabel.setText(String.format("50/50 applied: the secret is in the lower half (%d to %d).", low, high));
        } else {
            low = highHalfStart;
            high = highHalfEnd;
            feedbackLabel.setText(String.format("50/50 applied: the secret is in the upper half (%d to %d).", low, high));
        }

        // prune pool to new bounds
        prunePossibleNumbers();

        updateRoundLabels();
        updateHintButtons();

        // optionally show remaining numbers if small
        if (possibleNumbers.size() <= 20) {
            String list = possibleNumbers.stream().map(String::valueOf).collect(Collectors.joining(", "));
            showInfo("Remaining numbers: " + list);
        } else {
            feedbackLabel.setText(feedbackLabel.getText() + " " + possibleNumbers.size() + " numbers remain.");
        }
    }

    private void updateHintButtons() {
        oddEvenBtn.setDisable(usedOddEvenHint);
        narrowBtn.setDisable(usedNarrowHint);
        fiftyBtn.setDisable(used5050Hint);
    }

    private void endRound(boolean won) {
        attemptsLeft = 0;
        if (!won) {
            roundsPlayed++;
            currentStreak = 0;
        }
    }

    private int computeScore(int maxNum, int maxAttempts, int attemptsUsed) {
        double rangeFactor = Math.log(maxNum) / Math.log(2);
        int base = (int) Math.round(50 * rangeFactor);
        int attemptsBonus = Math.max(0, (maxAttempts - attemptsUsed) * 10);
        int streakBonus = Math.min(50, currentStreak * 5);
        return base + attemptsBonus + streakBonus;
    }

    private void showLeaderboardDialog() {
        List<LeaderboardEntry> entries = readLeaderboardSafe();
        entries.sort((a, b) -> Integer.compare(b.score, a.score));

        StringBuilder sb = new StringBuilder();
        if (entries.isEmpty()) sb.append("No leaderboard entries yet.");
        else {
            for (int i = 0; i < Math.min(10, entries.size()); i++) {
                LeaderboardEntry e = entries.get(i);
                sb.append(String.format("%d) %s - %d%n", i + 1, e.name, e.score));
            }
        }

        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(Font.font(14));
        ta.setWrapText(true);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Leaderboard");
        dialog.getDialogPane().setContent(ta);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    /**
     * SAVE method now writes to a stable directory under the user's home:
     *   %USERPROFILE%/NumberGuessingGame/leaderboard.txt
     *
     * This avoids OneDrive or project-workdir issues.
     */
    private void saveScoreToLeaderboard(int score) {
        // Ask for name (default is playerName)
        TextInputDialog td = new TextInputDialog(playerName);
        td.setTitle("Save Score");
        td.setHeaderText("Enter name to save score (leave blank to skip):");
        Optional<String> res = td.showAndWait();
        if (res.isEmpty()) return;
        String name = res.get().trim();
        if (name.isEmpty()) return;

        try {
            // Ensure directory exists
            if (!Files.exists(DEFAULT_LEADERBOARD_DIR)) {
                Files.createDirectories(DEFAULT_LEADERBOARD_DIR);
            }

            String entry = String.format("%s,%d,%d%n", name, score, System.currentTimeMillis());
            // Append safely (create file if missing)
            Files.write(DEFAULT_LEADERBOARD_FILE, entry.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            showInfo("Score saved to: " + DEFAULT_LEADERBOARD_FILE.toAbsolutePath());
        } catch (IOException ex) {
            showError("Failed saving leaderboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private List<LeaderboardEntry> readLeaderboardSafe() {
        try {
            return readLeaderboard();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private List<LeaderboardEntry> readLeaderboard() throws IOException {
        List<LeaderboardEntry> list = new ArrayList<>();
        Path p = DEFAULT_LEADERBOARD_FILE;
        if (!Files.exists(p)) return list;
        List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
        for (String l : lines) {
            String[] parts = l.split(",", 3);
            if (parts.length >= 2) {
                try {
                    int s = Integer.parseInt(parts[1]);
                    list.add(new LeaderboardEntry(parts[0], s));
                } catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.initOwner(primaryStage);
        a.showAndWait();
    }

    private boolean askConfirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.initOwner(primaryStage);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.YES;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.initOwner(primaryStage);
        a.showAndWait();
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String sessionStatsText() {
        return String.format("Rounds: %d   Wins: %d   Total score: %d   Best round: %d   Best streak: %d",
                roundsPlayed, totalWins, totalScore, bestRoundScore, bestStreak);
    }

    private void applyGlobalStyling(Scene scene) {
        scene.getRoot().setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14;");
    }

    // Leaderboard entry holder
    private static class LeaderboardEntry {
        String name;
        int score;
        LeaderboardEntry(String n, int s) { name = n; score = s; }
    }
}
