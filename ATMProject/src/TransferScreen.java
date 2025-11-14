import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TransferScreen {
    private Stage stage;
    private User currentUser;
    private ATMDAO atmDAO;

    public TransferScreen(Stage stage, User user) {
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
        Label titleLabel = new Label("↔️ TRANSFER FUNDS");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        // Panel
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(30));
        panel.setMaxWidth(500);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label balanceLabel = new Label(String.format("Available Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Recipient Account
        Label recipientLabel = new Label("Recipient Account Number:");
        recipientLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField recipientField = new TextField();
        recipientField.setPromptText("Enter account number (e.g., ACC002)");
        recipientField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Amount
        Label amountLabel = new Label("Transfer Amount:");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to transfer");
        amountField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Description
        Label descLabel = new Label("Description (Optional):");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField descField = new TextField();
        descField.setPromptText("Enter description");
        descField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button transferBtn = new Button("Transfer");
        transferBtn.setPrefWidth(150);
        transferBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;");
        transferBtn.setOnMouseEntered(e -> transferBtn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-cursor: hand;"));
        transferBtn.setOnMouseExited(e -> transferBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;"));
        transferBtn.setOnAction(e -> processTransfer(recipientField.getText(), amountField.getText(), descField.getText()));

        Button backBtn = new Button("← Back");
        backBtn.setPrefWidth(150);
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;");
        backBtn.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, currentUser);
            mainMenu.show();
        });

        buttonBox.getChildren().addAll(transferBtn, backBtn);

        panel.getChildren().addAll(
                balanceLabel,
                recipientLabel, recipientField,
                amountLabel, amountField,
                descLabel, descField,
                buttonBox
        );

        root.getChildren().addAll(titleLabel, panel);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ATM System - Transfer");
    }

    private void processTransfer(String recipientAccount, String amountStr, String description) {
        // Validation
        if (recipientAccount.trim().isEmpty() || amountStr.trim().isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (recipientAccount.equals(currentUser.getAccountNumber())) {
            showError("Cannot transfer to your own account");
            return;
        }

        if (!atmDAO.accountExists(recipientAccount)) {
            showError("Recipient account does not exist");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
            return;
        }

        if (amount <= 0) {
            showError("Amount must be greater than zero");
            return;
        }

        if (amount > currentUser.getBalance()) {
            showError("Insufficient funds!\nAvailable: $" + String.format("%.2f", currentUser.getBalance()));
            return;
        }

        // Process transfer
        double newBalance = currentUser.getBalance() - amount;
        if (atmDAO.updateBalance(currentUser.getAccountNumber(), newBalance)) {
            // Add transaction for sender
            Transaction senderTransaction = new Transaction(
                    currentUser.getAccountNumber(),
                    "TRANSFER",
                    amount,
                    description.isEmpty() ? "Transfer to " + recipientAccount : description
            );
            senderTransaction.setRecipientAccount(recipientAccount);
            atmDAO.addTransaction(senderTransaction);

            // Update recipient balance
            double recipientBalance = atmDAO.getBalance(recipientAccount);
            atmDAO.updateBalance(recipientAccount, recipientBalance + amount);

            // Add transaction for recipient
            Transaction recipientTransaction = new Transaction(
                    recipientAccount,
                    "TRANSFER_RECEIVED",
                    amount,
                    "Transfer from " + currentUser.getAccountNumber()
            );
            atmDAO.addTransaction(recipientTransaction);

            currentUser.setBalance(newBalance);

            showSuccess("Transfer Successful!\n\n" +
                    "Amount: $" + String.format("%.2f", amount) + "\n" +
                    "To: " + recipientAccount + "\n" +
                    "New Balance: $" + String.format("%.2f", newBalance));
        } else {
            showError("Transaction failed. Please try again.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Transfer Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Transfer Complete");
        alert.setContentText(message);
        alert.showAndWait();
    }
}