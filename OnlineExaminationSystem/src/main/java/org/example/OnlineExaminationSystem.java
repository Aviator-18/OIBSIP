package org.example;// File: OnlineExaminationSystem.java
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class User {
    private String username;
    private String password;
    private String name;
    private Map<Integer, Integer> examScores;

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.examScores = new HashMap<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public Map<Integer, Integer> getExamScores() { return examScores; }
    public void addScore(int examId, int score) { examScores.put(examId, score); }
}

class Question {
    private String question;
    private List<String> options;
    private int correctAnswer;

    public Question(String question, List<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswer() { return correctAnswer; }
}

class Exam {
    private int id;
    private String title;
    private List<Question> questions;
    private int timeLimit;

    public Exam(int id, String title, List<Question> questions, int timeLimit) {
        this.id = id;
        this.title = title;
        this.questions = questions;
        this.timeLimit = timeLimit;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public List<Question> getQuestions() { return questions; }
    public int getTimeLimit() { return timeLimit; }
}

public class OnlineExaminationSystem extends Application {
    private static Map<String, User> users = new HashMap<>();
    private static List<Exam> exams = new ArrayList<>();
    private User currentUser = null;
    private Stage primaryStage;
    private Timer examTimer;
    private Label timerLabel;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        initializeData();

        primaryStage.setTitle("Online Examination System");
        showLoginScreen();
        primaryStage.show();
    }

    private void initializeData() {
        users.put("student1", new User("student1", "pass123", "John Doe"));
        users.put("student2", new User("student2", "pass456", "Jane Smith"));

        // Exam 1: General Knowledge
        List<Question> gkQuestions = new ArrayList<>();
        gkQuestions.add(new Question(
                "What is the capital of France?",
                Arrays.asList("London", "Paris", "Berlin", "Madrid"), 1
        ));
        gkQuestions.add(new Question(
                "Which planet is known as the Red Planet?",
                Arrays.asList("Venus", "Mars", "Jupiter", "Saturn"), 1
        ));
        gkQuestions.add(new Question(
                "What is 2 + 2?",
                Arrays.asList("3", "4", "5", "6"), 1
        ));
        gkQuestions.add(new Question(
                "Who wrote 'Romeo and Juliet'?",
                Arrays.asList("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"), 1
        ));
        gkQuestions.add(new Question(
                "What is the largest ocean on Earth?",
                Arrays.asList("Atlantic", "Indian", "Arctic", "Pacific"), 3
        ));
        exams.add(new Exam(1, "General Knowledge Quiz", gkQuestions, 180));

        // Exam 2: Science Quiz
        List<Question> scienceQuestions = new ArrayList<>();
        scienceQuestions.add(new Question(
                "What is the chemical symbol for water?",
                Arrays.asList("H2O", "CO2", "O2", "N2"), 0
        ));
        scienceQuestions.add(new Question(
                "What is the speed of light?",
                Arrays.asList("300,000 km/s", "150,000 km/s", "450,000 km/s", "600,000 km/s"), 0
        ));
        scienceQuestions.add(new Question(
                "What is the powerhouse of the cell?",
                Arrays.asList("Nucleus", "Mitochondria", "Ribosome", "Chloroplast"), 1
        ));
        scienceQuestions.add(new Question(
                "What gas do plants absorb from the atmosphere?",
                Arrays.asList("Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"), 2
        ));
        scienceQuestions.add(new Question(
                "What is the smallest unit of matter?",
                Arrays.asList("Molecule", "Atom", "Cell", "Electron"), 1
        ));
        scienceQuestions.add(new Question(
                "What is the boiling point of water in Celsius?",
                Arrays.asList("50°C", "75°C", "100°C", "125°C"), 2
        ));
        exams.add(new Exam(2, "Science Quiz", scienceQuestions, 240));

        // Exam 3: Programming Basics
        List<Question> progQuestions = new ArrayList<>();
        progQuestions.add(new Question(
                "What does HTML stand for?",
                Arrays.asList("Hyper Text Markup Language", "High Tech Modern Language",
                        "Home Tool Markup Language", "Hyperlinks Text Mark Language"), 0
        ));
        progQuestions.add(new Question(
                "Which language is known as the 'mother of all languages'?",
                Arrays.asList("Python", "Java", "C", "Assembly"), 2
        ));
        progQuestions.add(new Question(
                "What does CPU stand for?",
                Arrays.asList("Central Process Unit", "Central Processing Unit",
                        "Computer Personal Unit", "Central Processor Unit"), 1
        ));
        progQuestions.add(new Question(
                "What is the main function of RAM?",
                Arrays.asList("Permanent storage", "Temporary storage", "Processing", "Output"), 1
        ));
        progQuestions.add(new Question(
                "Which of these is NOT a programming language?",
                Arrays.asList("Python", "Java", "HTML", "C++"), 2
        ));
        progQuestions.add(new Question(
                "What does SQL stand for?",
                Arrays.asList("Structured Query Language", "Simple Query Language",
                        "Standard Question Language", "System Query Language"), 0
        ));
        progQuestions.add(new Question(
                "What is the binary representation of decimal 10?",
                Arrays.asList("1010", "1100", "1001", "1111"), 0
        ));
        exams.add(new Exam(3, "Programming Basics", progQuestions, 300));
    }

    private void showLoginScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("Online Examination System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Login to Continue");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setStyle("-fx-font-size: 14px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-font-size: 14px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        loginButton.setMaxWidth(300);

        Button registerButton = new Button("Register New Account");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        registerButton.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            User user = users.get(username);

            if (user != null && user.getPassword().equals(password)) {
                currentUser = user;
                showMainMenu();
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        });

        registerButton.setOnAction(e -> showRegisterScreen());

        layout.getChildren().addAll(titleLabel, subtitleLabel, usernameField,
                passwordField, loginButton, registerButton, messageLabel);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showRegisterScreen() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("Register New Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(300);

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        registerButton.setMaxWidth(300);

        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        backButton.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.LIGHTGREEN);

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("All fields are required!");
            } else if (users.containsKey(username)) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Username already exists!");
            } else {
                users.put(username, new User(username, password, name));
                messageLabel.setTextFill(Color.LIGHTGREEN);
                messageLabel.setText("Registration successful! Please login.");
            }
        });

        backButton.setOnAction(e -> showLoginScreen());

        layout.getChildren().addAll(titleLabel, usernameField, passwordField,
                nameField, registerButton, backButton, messageLabel);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showMainMenu() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #34495e;");

        VBox topBar = new VBox(10);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #2c3e50;");

        Label welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        welcomeLabel.setTextFill(Color.WHITE);

        topBar.getChildren().add(welcomeLabel);
        layout.setTop(topBar);

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(40));
        centerBox.setAlignment(Pos.CENTER);

        Label menuTitle = new Label("Main Menu");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        menuTitle.setTextFill(Color.WHITE);

        Button takeExamButton = createMenuButton("Take Exam", "#3498db");
        Button viewResultsButton = createMenuButton("View Results", "#9b59b6");
        Button updateProfileButton = createMenuButton("Update Profile", "#e67e22");
        Button changePasswordButton = createMenuButton("Change Password", "#e74c3c");
        Button logoutButton = createMenuButton("Logout", "#95a5a6");

        takeExamButton.setOnAction(e -> showExamSelection());
        viewResultsButton.setOnAction(e -> showResults());
        updateProfileButton.setOnAction(e -> showUpdateProfile());
        changePasswordButton.setOnAction(e -> showChangePassword());
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        centerBox.getChildren().addAll(menuTitle, takeExamButton, viewResultsButton,
                updateProfileButton, changePasswordButton, logoutButton);
        layout.setCenter(centerBox);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 40;");
        button.setMaxWidth(350);
        button.setMinHeight(50);
        return button;
    }

    private void showExamSelection() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #34495e;");

        Label titleLabel = new Label("Select an Exam");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        VBox examsBox = new VBox(15);
        examsBox.setAlignment(Pos.CENTER);

        for (Exam exam : exams) {
            VBox examCard = new VBox(10);
            examCard.setPadding(new Insets(20));
            examCard.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10;");
            examCard.setMaxWidth(500);

            Label examTitle = new Label(exam.getTitle());
            examTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            examTitle.setTextFill(Color.WHITE);

            Label examInfo = new Label("Questions: " + exam.getQuestions().size() +
                    " | Time: " + (exam.getTimeLimit() / 60) + " minutes");
            examInfo.setTextFill(Color.LIGHTGRAY);

            Button startButton = new Button("Start Exam");
            startButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
            startButton.setOnAction(e -> startExam(exam));

            examCard.getChildren().addAll(examTitle, examInfo, startButton);
            examsBox.getChildren().add(examCard);
        }

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        backButton.setOnAction(e -> showMainMenu());

        ScrollPane scrollPane = new ScrollPane(examsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #34495e; -fx-background-color: transparent;");

        layout.getChildren().addAll(titleLabel, scrollPane, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void startExam(Exam exam) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #ecf0f1;");

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(topBar, Priority.ALWAYS);

        Label examTitleLabel = new Label(exam.getTitle());
        examTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        examTitleLabel.setTextFill(Color.WHITE);

        timerLabel = new Label("Time: " + formatTime(exam.getTimeLimit()));
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        timerLabel.setTextFill(Color.YELLOW);
        timerLabel.setStyle("-fx-background-color: #e74c3c; -fx-padding: 10; -fx-background-radius: 5;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(examTitleLabel, spacer, timerLabel);
        layout.setTop(topBar);

        VBox questionsBox = new VBox(25);
        questionsBox.setPadding(new Insets(30));

        List<ToggleGroup> answerGroups = new ArrayList<>();
        AtomicInteger questionNumber = new AtomicInteger(1);

        for (Question question : exam.getQuestions()) {
            VBox questionCard = new VBox(12);
            questionCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            Label questionLabel = new Label("Q" + questionNumber.getAndIncrement() + ". " + question.getQuestion());
            questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            questionLabel.setWrapText(true);

            ToggleGroup group = new ToggleGroup();
            answerGroups.add(group);

            VBox optionsBox = new VBox(8);
            for (int i = 0; i < question.getOptions().size(); i++) {
                RadioButton rb = new RadioButton(question.getOptions().get(i));
                rb.setToggleGroup(group);
                rb.setUserData(i);
                rb.setFont(Font.font("Arial", 13));
                optionsBox.getChildren().add(rb);
            }

            questionCard.getChildren().addAll(questionLabel, optionsBox);
            questionsBox.getChildren().add(questionCard);
        }

        Button submitButton = new Button("Submit Exam");
        submitButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 40; -fx-font-weight: bold;");
        submitButton.setMaxWidth(200);

        VBox submitBox = new VBox(submitButton);
        submitBox.setAlignment(Pos.CENTER);
        submitBox.setPadding(new Insets(20));
        questionsBox.getChildren().add(submitBox);

        ScrollPane scrollPane = new ScrollPane(questionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #ecf0f1; -fx-background-color: #ecf0f1;");
        layout.setCenter(scrollPane);

        final boolean[] submitted = {false};

        submitButton.setOnAction(e -> {
            if (!submitted[0]) {
                submitted[0] = true;
                if (examTimer != null) {
                    examTimer.cancel();
                }
                submitExam(exam, answerGroups);
            }
        });

        Scene scene = new Scene(layout, 900, 700);
        primaryStage.setScene(scene);

        startTimer(exam.getTimeLimit(), () -> {
            if (!submitted[0]) {
                submitted[0] = true;
                Platform.runLater(() -> submitExam(exam, answerGroups));
            }
        });
    }

    private void startTimer(int seconds, Runnable onComplete) {
        if (examTimer != null) {
            examTimer.cancel();
        }

        examTimer = new Timer();
        final int[] timeRemaining = {seconds};

        examTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeRemaining[0]--;
                Platform.runLater(() -> {
                    if (timeRemaining[0] <= 0) {
                        examTimer.cancel();
                        onComplete.run();
                    } else {
                        timerLabel.setText("Time: " + formatTime(timeRemaining[0]));
                        if (timeRemaining[0] <= 30) {
                            timerLabel.setStyle("-fx-background-color: #c0392b; -fx-padding: 10; -fx-background-radius: 5;");
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void submitExam(Exam exam, List<ToggleGroup> answerGroups) {
        int score = 0;
        for (int i = 0; i < exam.getQuestions().size(); i++) {
            ToggleGroup group = answerGroups.get(i);
            if (group.getSelectedToggle() != null) {
                int answer = (int) group.getSelectedToggle().getUserData();
                if (answer == exam.getQuestions().get(i).getCorrectAnswer()) {
                    score++;
                }
            }
        }

        currentUser.addScore(exam.getId(), score);
        showExamResults(exam, score);
    }

    private void showExamResults(Exam exam, int score) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("Exam Complete!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        VBox resultCard = new VBox(15);
        resultCard.setPadding(new Insets(30));
        resultCard.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        resultCard.setMaxWidth(400);
        resultCard.setAlignment(Pos.CENTER);

        Label examNameLabel = new Label(exam.getTitle());
        examNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label scoreLabel = new Label("Your Score: " + score + "/" + exam.getQuestions().size());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.web("#27ae60"));

        double percentage = (score * 100.0) / exam.getQuestions().size();
        Label percentageLabel = new Label(String.format("Percentage: %.2f%%", percentage));
        percentageLabel.setFont(Font.font("Arial", 18));

        String grade = percentage >= 90 ? "Excellent!" : percentage >= 75 ? "Good!" :
                percentage >= 60 ? "Pass" : "Need Improvement";
        Label gradeLabel = new Label(grade);
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gradeLabel.setTextFill(percentage >= 60 ? Color.web("#27ae60") : Color.web("#e74c3c"));

        resultCard.getChildren().addAll(examNameLabel, scoreLabel, percentageLabel, gradeLabel);

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 40;");
        backButton.setOnAction(e -> showMainMenu());

        layout.getChildren().addAll(titleLabel, resultCard, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showResults() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #34495e;");

        Label titleLabel = new Label("Your Exam Results");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        VBox resultsBox = new VBox(15);
        resultsBox.setAlignment(Pos.CENTER);

        Map<Integer, Integer> scores = currentUser.getExamScores();

        if (scores.isEmpty()) {
            Label noResultsLabel = new Label("You haven't taken any exams yet.");
            noResultsLabel.setFont(Font.font("Arial", 16));
            noResultsLabel.setTextFill(Color.LIGHTGRAY);
            resultsBox.getChildren().add(noResultsLabel);
        } else {
            for (Map.Entry<Integer, Integer> entry : scores.entrySet()) {
                Exam exam = exams.stream()
                        .filter(e -> e.getId() == entry.getKey())
                        .findFirst()
                        .orElse(null);

                if (exam != null) {
                    HBox resultCard = new HBox(20);
                    resultCard.setPadding(new Insets(20));
                    resultCard.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 8;");
                    resultCard.setMaxWidth(600);
                    resultCard.setAlignment(Pos.CENTER_LEFT);

                    Label examLabel = new Label(exam.getTitle());
                    examLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    examLabel.setTextFill(Color.WHITE);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label scoreLabel = new Label(entry.getValue() + "/" + exam.getQuestions().size());
                    scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                    scoreLabel.setTextFill(Color.LIGHTGREEN);

                    resultCard.getChildren().addAll(examLabel, spacer, scoreLabel);
                    resultsBox.getChildren().add(resultCard);
                }
            }
        }

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        backButton.setOnAction(e -> showMainMenu());

        layout.getChildren().addAll(titleLabel, resultsBox, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showUpdateProfile() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #34495e;");

        Label titleLabel = new Label("Update Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-font-size: 14px;");

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        saveButton.setMaxWidth(300);

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        backButton.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.LIGHTGREEN);

        saveButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                currentUser.setName(newName);
                messageLabel.setText("Profile updated successfully!");
            }
        });

        backButton.setOnAction(e -> showMainMenu());

        layout.getChildren().addAll(titleLabel, nameField, saveButton, backButton, messageLabel);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showChangePassword() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #34495e;");

        Label titleLabel = new Label("Change Password");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.setMaxWidth(300);
        currentPasswordField.setStyle("-fx-font-size: 14px;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setMaxWidth(300);
        newPasswordField.setStyle("-fx-font-size: 14px;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        confirmPasswordField.setMaxWidth(300);
        confirmPasswordField.setStyle("-fx-font-size: 14px;");

        Button changeButton = new Button("Change Password");
        changeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        changeButton.setMaxWidth(300);

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        backButton.setMaxWidth(300);

        Label messageLabel = new Label();

        changeButton.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!currentPassword.equals(currentUser.getPassword())) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Current password is incorrect!");
            } else if (newPassword.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("New password cannot be empty!");
            } else if (!newPassword.equals(confirmPassword)) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Passwords do not match!");
            } else {
                currentUser.setPassword(newPassword);
                messageLabel.setTextFill(Color.LIGHTGREEN);
                messageLabel.setText("Password changed successfully!");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            }
        });

        backButton.setOnAction(e -> showMainMenu());

        layout.getChildren().addAll(titleLabel, currentPasswordField, newPasswordField,
                confirmPasswordField, changeButton, backButton, messageLabel);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}