# 🏧 ATM System - Java & JavaFX

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A modern, feature-rich ATM (Automated Teller Machine) system built with JavaFX for the GUI and MySQL for data persistence. This project demonstrates a complete banking transaction system with a professional user interface and robust database integration.

## 📋 Table of Contents

- [Features](#-features)
- [Screenshots](#-screenshots)
- [Technologies Used](#-technologies-used)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Database Setup](#-database-setup)
- [Running the Application](#-running-the-application)
- [Demo Accounts](#-demo-accounts)
- [Project Structure](#-project-structure)
- [Usage Guide](#-usage-guide)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

## ✨ Features

### 🔐 Authentication System
- Secure login with User ID and PIN verification
- Input validation and error handling
- Session management

### 💰 Financial Operations
- **Cash Withdrawal** - Quick buttons and custom amount options
- **Cash Deposit** - Multiple denomination support
- **Fund Transfer** - Transfer between accounts with validation
- **Balance Inquiry** - Real-time balance checking

### 📊 Transaction Management
- Complete transaction history tracking
- Detailed transaction records with timestamps
- Filter and view past transactions
- Export-ready transaction logs

### 🎨 User Interface
- Modern, responsive JavaFX GUI
- Intuitive navigation and user experience
- Professional gradient themes
- Real-time balance updates
- Interactive hover effects

### 🔒 Security Features
- PIN-based authentication
- SQL injection prevention with PreparedStatements
- Session timeout handling
- Secure database connections

## 📸 Screenshots

> Add screenshots here after deployment

## 🛠 Technologies Used

- **Frontend**: JavaFX 17
- **Backend**: Java 17 (JDK)
- **Database**: MySQL 8.0
- **JDBC**: MySQL Connector/J 8.x
- **IDE**: IntelliJ IDEA (recommended)
- **Build Tool**: Native Java compilation
- **Version Control**: Git & GitHub

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
  - Verify: `java -version`

- **JavaFX SDK** 17 or higher
  - Download from [Gluon](https://gluonhq.com/products/javafx/)
  - Extract to a known location

- **MySQL Server** 8.0 or higher
  - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
  - Remember your root password!

- **MySQL Connector/J** 8.x
  - Download from [MySQL Connector](https://dev.mysql.com/downloads/connector/j/)
  - JAR file included in `lib/` folder

- **IDE** (Optional but recommended)
  - IntelliJ IDEA, Eclipse, or VS Code
  - Or compile via command line

## 🚀 Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/Aviator-18/OIBSIP.git
cd OIBSIP/ATM-System
```

### Step 2: Set Up JavaFX

1. Download JavaFX SDK from [Gluon](https://gluonhq.com/products/javafx/)
2. Extract to a directory (e.g., `C:\javafx-sdk-17`)
3. Note the path to the `lib` folder

### Step 3: Configure Database Connection

1. Open `src/DatabaseConfig.java`
2. Update the following line with your MySQL password:

```java
private static final String PASSWORD = "your_mysql_password";
```

## 💾 Database Setup

### Option 1: Using MySQL Workbench (Recommended)

1. Open MySQL Workbench
2. Connect to your local MySQL instance
3. Open the file: `database/atm_database.sql`
4. Click Execute (⚡ icon)
5. Verify: You should see `atm_system` database with 50 users

### Option 2: Using Command Line

```bash
mysql -u root -p < database/atm_database.sql
```

Enter your MySQL password when prompted.

### Verify Database

```sql
USE atm_system;
SELECT COUNT(*) FROM users;  -- Should return 50
SELECT COUNT(*) FROM transactions;  -- Should return 10
```

## ▶️ Running the Application

### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Right-click on `src/ATMApplication.java`
3. Select **Modify Run Configuration**
4. Add VM options:

```
--module-path "path/to/javafx-sdk-17/lib" --add-modules javafx.controls,javafx.fxml
```

Replace `path/to/javafx-sdk-17/lib` with your actual JavaFX SDK path.

5. Click **Run** ▶️

### Using Command Line

**Compile:**
```bash
javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp "lib/*" -d out src/*.java
```

**Run:**
```bash
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp "out:lib/*" ATMApplication
```

## 🎭 Demo Accounts

Try the system with these pre-loaded accounts:

| User ID  | PIN  | Account Number | Balance     | Account Holder      |
|----------|------|----------------|-------------|---------------------|
| user001  | 1234 | ACC001         | $15,000.00  | John Smith          |
| user002  | 2345 | ACC002         | $22,500.50  | Emma Johnson        |
| user003  | 3456 | ACC003         | $8,750.25   | Michael Williams    |
| user010  | 0123 | ACC010         | $33,500.00  | Isabella Lopez      |
| user020  | 0000 | ACC020         | $17,500.75  | Evelyn Lee          |

**Test Scenarios:**
1. Login as `user001` (PIN: `1234`)
2. Withdraw $500
3. Check transaction history
4. Transfer $100 to `ACC002`
5. Deposit $200
6. View updated balance

## 📁 Project Structure

```
ATM-System/
├── src/
│   ├── ATMApplication.java          # Main application entry point
│   ├── LoginScreen.java             # Login interface
│   ├── MainMenuScreen.java          # Main menu with operations
│   ├── WithdrawScreen.java          # Withdrawal interface
│   ├── DepositScreen.java           # Deposit interface
│   ├── TransferScreen.java          # Transfer interface
│   ├── TransactionHistoryScreen.java # Transaction history view
│   ├── User.java                    # User data model
│   ├── Transaction.java             # Transaction data model
│   ├── ATMDAO.java                  # Database operations layer
│   └── DatabaseConfig.java          # Database configuration
├── lib/
│   └── mysql-connector-j-8.x.xx.jar # MySQL JDBC driver
├── database/
│   └── atm_database.sql             # Database schema & sample data
├── docs/
│   └── screenshots/                 # Application screenshots
├── README.md                        # This file
└── .gitignore                       # Git ignore rules
```

## 📖 Usage Guide

### 1. Login
- Enter your User ID (e.g., `user001`)
- Enter your 4-digit PIN (e.g., `1234`)
- Click **LOGIN**

### 2. Main Menu
Choose from 6 operations:
- 💵 **Withdraw** - Remove cash from account
- 💰 **Deposit** - Add cash to account
- ↔️ **Transfer** - Send money to another account
- 📋 **Transaction History** - View past transactions
- 💳 **Check Balance** - View current balance
- 🚪 **Exit** - Logout and return to login

### 3. Withdraw Money
- Select a quick amount ($20, $50, $100, etc.)
- Or enter a custom amount
- Confirm transaction
- Collect cash (simulated)

### 4. Deposit Money
- Select or enter amount
- Confirm deposit
- Receipt displayed with new balance

### 5. Transfer Funds
- Enter recipient account number (e.g., `ACC002`)
- Enter transfer amount
- Add description (optional)
- Confirm transfer
- Both accounts updated

### 6. View History
- See all your transactions
- Sorted by date (newest first)
- Shows: Date, Type, Amount, Description

## 🔧 Configuration

### Database Settings

Edit `src/DatabaseConfig.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/atm_system";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### UI Customization

Colors and styles can be modified in each screen class:
- Background gradients
- Button colors
- Font sizes
- Layout spacing

## 🐛 Troubleshooting

### Issue: "JavaFX runtime components are missing"
**Solution:** Add VM options with correct JavaFX path

### Issue: "Access denied for user 'root'"
**Solution:** Check MySQL password in `DatabaseConfig.java`

### Issue: "Communications link failure"
**Solution:** Ensure MySQL service is running

### Issue: "Table doesn't exist"
**Solution:** Run the SQL script to create database

For more help, see [Issues](https://github.com/Aviator-18/OIBSIP/issues)

## 🤝 Contributing

Contributions are welcome! Here's how:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java naming conventions
- Add comments to complex logic
- Test all features before committing
- Update README if adding new features

## 🔮 Future Enhancements

- [ ] Receipt generation (PDF export)
- [ ] Email notifications for transactions
- [ ] Multi-language support
- [ ] Bill payment integration
- [ ] Account statement generation
- [ ] PIN change functionality
- [ ] Account freeze/unfreeze
- [ ] Admin panel for user management
- [ ] Mobile app version
- [ ] Biometric authentication

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@Aviator-18](https://github.com/Aviator-18)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/your-profile)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Built as part of internship project at Oasis Infobyte
- JavaFX documentation and community
- MySQL documentation
- Stack Overflow community

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Search existing [Issues](https://github.com/Aviator-18/OIBSIP/issues)
3. Create a new issue with:
   - Clear description
   - Steps to reproduce
   - Screenshots (if applicable)
   - Error messages

## ⭐ Show Your Support

If you found this project helpful, please give it a ⭐️!

---

**Made with ❤️ for learning and development**

*Last Updated: November 2024*
