import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DepositScreen {
    private Stage stage;
    private User currentUser;
    private ATMDAO atmDAO;

    public DepositScreen(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.atmDAO = new ATMDAO();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        // Title
        Label titleLabel = new Label("💰 DEPOSIT CASH");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        // Panel
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(30));
        panel.setMaxWidth(500);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label balanceLabel = new Label(String.format("Current Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label infoLabel = new Label("Select amount or enter custom amount:");
        infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        // Quick amount buttons
        GridPane amountGrid = new GridPane();
        amountGrid.setAlignment(Pos.CENTER);
        amountGrid.setHgap(15);
        amountGrid.setVgap(15);

        String[] amounts = {"$50", "$100", "$200", "$500", "$1000", "$2000"};
        int[] values = {50, 100, 200, 500, 1000, 2000};

        for (int i = 0; i < amounts.length; i++) {
            final int amount = values[i];
            Button btn = new Button(amounts[i]);
            btn.setPrefSize(120, 50);
            btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
            btn.setOnAction(e -> processDeposit(amount));
            amountGrid.add(btn, i % 3, i / 3);
        }

        // Custom amount
        HBox customBox = new HBox(10);
        customBox.setAlignment(Pos.CENTER);

        Label customLabel = new Label("Custom Amount: $");
        customLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField customField = new TextField();
        customField.setPromptText("Enter amount");
        customField.setPrefWidth(150);
        customField.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

        Button customBtn = new Button("Deposit");
        customBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        customBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(customField.getText());
                processDeposit(amount);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount");
            }
        });

        customBox.getChildren().addAll(customLabel, customField, customBtn);

        // Back button
        Button backBtn = new Button("← Back to Menu");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 5;");
        backBtn.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, currentUser);
            mainMenu.show();
        });

        panel.getChildren().addAll(balanceLabel, infoLabel, amountGrid, customBox, backBtn);
        root.getChildren().addAll(titleLabel, panel);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ATM System - Deposit");
    }

    private void processDeposit(double amount) {
        if (amount <= 0) {
            showError("Amount must be greater than zero");
            return;
        }

        double newBalance = currentUser.getBalance() + amount;
        if (atmDAO.updateBalance(currentUser.getAccountNumber(), newBalance)) {
            Transaction transaction = new Transaction(
                    currentUser.getAccountNumber(),
                    "DEPOSIT",
                    amount,
                    "Cash Deposit"
            );
            atmDAO.addTransaction(transaction);
            currentUser.setBalance(newBalance);

            showSuccess("Deposit Successful!\n\nAmount: $" + String.format("%.2f", amount) +
                    "\nNew Balance: $" + String.format("%.2f", newBalance));
        } else {
            showError("Transaction failed. Please try again.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Deposit Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Transaction Complete");
        alert.setContentText(message);
        alert.showAndWait();
    }
}