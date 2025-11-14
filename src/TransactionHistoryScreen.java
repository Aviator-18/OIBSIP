import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionHistoryScreen {
    private Stage stage;
    private User currentUser;
    private ATMDAO atmDAO;

    public TransactionHistoryScreen(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.atmDAO = new ATMDAO();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        // Top section
        VBox topSection = new VBox(15);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(30));

        Label titleLabel = new Label("📋 TRANSACTION HISTORY");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        Label accountLabel = new Label("Account: " + currentUser.getAccountNumber() + " - " + currentUser.getAccountHolder());
        accountLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        accountLabel.setTextFill(Color.LIGHTGRAY);

        Label balanceLabel = new Label(String.format("Current Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        balanceLabel.setTextFill(Color.LIGHTGREEN);

        topSection.getChildren().addAll(titleLabel, accountLabel, balanceLabel);

        // Table
        TableView<TransactionRow> table = new TableView<>();
        table.setStyle("-fx-background-color: white;");

        TableColumn<TransactionRow, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(200);

        TableColumn<TransactionRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        TableColumn<TransactionRow, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(150);

        TableColumn<TransactionRow, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        table.getColumns().addAll(dateCol, typeCol, amountCol, descCol);

        // Load transaction data
        List<Transaction> transactions = atmDAO.getTransactionHistory(currentUser.getAccountNumber());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Transaction t : transactions) {
            String date = dateFormat.format(t.getTransactionDate());
            String type = t.getTransactionType();
            String amount = String.format("$%.2f", t.getAmount());
            String desc = t.getDescription();

            table.getItems().add(new TransactionRow(date, type, amount, desc));
        }

        if (transactions.isEmpty()) {
            Label noDataLabel = new Label("No transactions found");
            noDataLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            table.setPlaceholder(noDataLabel);
        }

        // Bottom section
        HBox bottomSection = new HBox();
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));

        Button backBtn = new Button("← Back to Menu");
        backBtn.setPrefWidth(200);
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 5;"));
        backBtn.setOnAction(e -> {
            MainMenuScreen mainMenu = new MainMenuScreen(stage, currentUser);
            mainMenu.show();
        });

        bottomSection.getChildren().add(backBtn);

        root.setTop(topSection);
        root.setCenter(table);
        root.setBottom(bottomSection);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ATM System - Transaction History");
    }

    // Inner class for TableView rows
    public static class TransactionRow {
        private String date;
        private String type;
        private String amount;
        private String description;

        public TransactionRow(String date, String type, String amount, String description) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.description = description;
        }

        public String getDate() { return date; }
        public String getType() { return type; }
        public String getAmount() { return amount; }
        public String getDescription() { return description; }
    }
}