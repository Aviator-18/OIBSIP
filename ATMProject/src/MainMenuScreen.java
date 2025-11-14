import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainMenuScreen {
    private Stage stage;
    private User currentUser;
    private ATMDAO atmDAO;

    public MainMenuScreen(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.atmDAO = new ATMDAO();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        // Top Bar - User Info
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label welcomeLabel = new Label("Welcome, " + currentUser.getAccountHolder());
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        welcomeLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label balanceLabel = new Label(String.format("Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        balanceLabel.setTextFill(Color.LIGHTGREEN);

        topBar.getChildren().addAll(welcomeLabel, spacer, balanceLabel);

        // Center - Menu Options
        VBox menuPanel = new VBox(20);
        menuPanel.setAlignment(Pos.CENTER);
        menuPanel.setPadding(new Insets(40));

        Label menuTitle = new Label("Please Select a Transaction");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        menuTitle.setTextFill(Color.WHITE);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);

        // Create Menu Buttons
        Button withdrawBtn = createMenuButton("WITHDRAW", "💵");
        Button depositBtn = createMenuButton("DEPOSIT", "💰");
        Button transferBtn = createMenuButton("TRANSFER", "↔️");
        Button historyBtn = createMenuButton("TRANSACTION\nHISTORY", "📋");
        Button balanceBtn = createMenuButton("CHECK\nBALANCE", "💳");
        Button exitBtn = createMenuButton("EXIT", "🚪");

        // Button Actions
        withdrawBtn.setOnAction(e -> {
            WithdrawScreen withdrawScreen = new WithdrawScreen(stage, currentUser);
            withdrawScreen.show();
        });

        depositBtn.setOnAction(e -> {
            DepositScreen depositScreen = new DepositScreen(stage, currentUser);
            depositScreen.show();
        });

        transferBtn.setOnAction(e -> {
            TransferScreen transferScreen = new TransferScreen(stage, currentUser);
            transferScreen.show();
        });

        historyBtn.setOnAction(e -> {
            TransactionHistoryScreen historyScreen = new TransactionHistoryScreen(stage, currentUser);
            historyScreen.show();
        });

        balanceBtn.setOnAction(e -> {
            showBalanceDialog();
        });

        exitBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("You will be logged out of the system.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                LoginScreen loginScreen = new LoginScreen(stage);
                loginScreen.show();
            }
        });

        // Add buttons to grid
        buttonGrid.add(withdrawBtn, 0, 0);
        buttonGrid.add(depositBtn, 1, 0);
        buttonGrid.add(transferBtn, 0, 1);
        buttonGrid.add(historyBtn, 1, 1);
        buttonGrid.add(balanceBtn, 0, 2);
        buttonGrid.add(exitBtn, 1, 2);

        menuPanel.getChildren().addAll(menuTitle, buttonGrid);

        root.setTop(topBar);
        root.setCenter(menuPanel);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ATM System - Main Menu");
    }

    private Button createMenuButton(String text, String icon) {
        Button button = new Button(icon + "\n" + text);
        button.setPrefSize(200, 120);
        button.setStyle("-fx-background-color: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5); -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"));
        return button;
    }

    private void showBalanceDialog() {
        double currentBalance = atmDAO.getBalance(currentUser.getAccountNumber());
        currentUser.setBalance(currentBalance);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Balance");
        alert.setHeaderText("Current Balance Information");
        alert.setContentText(String.format("Account: %s\nHolder: %s\nBalance: $%.2f",
                currentUser.getAccountNumber(),
                currentUser.getAccountHolder(),
                currentBalance));
        alert.showAndWait();
    }
}