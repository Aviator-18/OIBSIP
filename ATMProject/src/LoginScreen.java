import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScreen {
    private Stage stage;
    private ATMDAO atmDAO;

    public LoginScreen(Stage stage) {
        this.stage = stage;
        this.atmDAO = new ATMDAO();
    }

    public void show() {
        // Main container
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        // ATM Header
        Label titleLabel = new Label("ATM SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Welcome! Please login to continue");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        // Login Panel
        VBox loginPanel = new VBox(15);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(30));
        loginPanel.setMaxWidth(400);
        loginPanel.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // User ID Field
        Label userIdLabel = new Label("User ID:");
        userIdLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter your User ID");
        userIdField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // PIN Field
        Label pinLabel = new Label("PIN:");
        pinLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter your 4-digit PIN");
        pinField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Error Label
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        // Login Button
        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(200);
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;"));

        loginButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            String pin = pinField.getText().trim();

            if (userId.isEmpty() || pin.isEmpty()) {
                errorLabel.setText("Please enter both User ID and PIN");
                errorLabel.setVisible(true);
                return;
            }

            User user = atmDAO.authenticateUser(userId, pin);
            if (user != null) {
                MainMenuScreen mainMenu = new MainMenuScreen(stage, user);
                mainMenu.show();
            } else {
                errorLabel.setText("Invalid User ID or PIN. Please try again.");
                errorLabel.setVisible(true);
                pinField.clear();
            }
        });

        // Enter key support
        pinField.setOnAction(e -> loginButton.fire());

        // Demo Info
        Label demoLabel = new Label("Demo: user001 / PIN: 1234");
        demoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        demoLabel.setTextFill(Color.GRAY);

        loginPanel.getChildren().addAll(
                userIdLabel, userIdField,
                pinLabel, pinField,
                errorLabel,
                loginButton,
                demoLabel
        );

        root.getChildren().addAll(titleLabel, subtitleLabel, loginPanel);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("ATM System - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}