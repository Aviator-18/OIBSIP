# 🎯 Number Guessing Game (JavaFX)

A modern, fully interactive **Number Guessing Game** built using **JavaFX**, featuring intelligent hints, difficulty levels, scoring, persistent leaderboard, and a clean UI.  
This project is part of the **OIBSIP Java Development Internship**.

---

## 📌 Features

### 🎮 Game Mechanics
- Random number generated each round
- Multiple difficulty levels:
  - **Easy:** 1–50, 10 attempts  
  - **Medium:** 1–100, 7 attempts  
  - **Hard:** 1–200, 5 attempts  
  - **Custom:** player sets number range & attempts
- Real-time feedback: "Higher" / "Lower"
- Range dynamically narrows based on guesses

### 💡 Smart Hint System
You get **3 special hints per round**:

| Hint Type | Description |
|----------|-------------|
| **Odd/Even** | Reveals whether the secret number is odd or even |
| **Narrow Range** | Narrows the search area around the secret number |
| **50/50 Split** | Removes half the possible numbers (upper or lower half) |

Each hint can only be used **once per round**.

### 🏆 Leaderboard System
- Stores top scores in a persistent file located at:  
  `~/NumberGuessingGame/leaderboard.txt`
- Automatically loads and displays the highest-scoring players
- Includes timestamp & player name
- Saved **outside the project folder**, so it survives rebuilds

### 📊 Session Stats (Displayed on Main Menu)
- Total rounds played  
- Total wins  
- Total score  
- Best round score  
- Best win streak  

### ✨ UI & Experience
- Modern JavaFX UI  
- Dialogs for hints, results, confirmation, and leaderboard  
- Keyboard `ENTER` support for fast guessing  
- Smooth layout & styling  

---

## 🖼️ Screenshots

### 📌 1. Main Menu and Difficulty Selection
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/f1ad16b6-f0f4-41a3-b81d-b4282a38f840" />

### 📌 2. Gameplay Screen  
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/5a4690df-db73-4609-a842-7036e4c84d28" />

### 📌 3. Hints in Action  
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/fb725c7d-36e0-448f-91fc-909d7cdd31d4" />

### 📌 4. Win / Loss Dialog  
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/f07c3bf5-0596-4c50-ad25-dbcc71bd05d5" />

### 📌 5. Leaderboard  
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/add70e04-05bf-4003-a467-4d94af29ec89" />

### 📌 6. Custom Difficulty
<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/c58917d3-a301-4786-85e8-80d6d0c0659f" />


---

## 🚀 How to Run the Game

### ✔️ Requirements
- **JDK 17+ (Recommended: JDK-21 or JDK-23)**
- **JavaFX SDK (matching your JDK version)**  
  Example:  
  `openjfx-25.0.1-sdk`

### ✔️ Project Structure
```
/src
 └── NumberGuessingGameFX.java
/screenshots (you create this)
/README.md
```

---

## ▶️ Running via IntelliJ IDEA (Recommended)

1. Download & extract **JavaFX SDK**  
   Example path:  
   `C:\Users\Vasudev\Downloads\javafx-sdk-25.0.1\lib`

2. Go to:
   ```
   Run → Edit Configurations → VM Options
   ```

3. Add this exactly:
   ```
   --module-path "C:\path\to\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml
   ```

4. Click **Apply → Run**

---

## ▶️ Running via Command Line

```
java --module-path "C:\path\to\javafx-sdk-25.0.1\lib" \
     --add-modules javafx.controls,javafx.fxml \
     -classpath out/production/ProjectName \
     NumberGuessingGameFX
```

---

## 📐 Scoring System

Score per round is calculated using:

- Range difficulty (`log2(max number)`)
- Number of attempts saved
- Win streak bonuses  
- Minimum score = **difficulty-dependent base score**
- Maximum score = **base + attempts bonus + streak bonus**

This makes higher difficulty & better efficiency more rewarding.

---

## 🗄️ Leaderboard Storage Format
Leaderboard file location:
```
C:/Users/<YourName>/NumberGuessingGame/leaderboard.txt
```

Format:
```
playerName,score,timestamp
```

Example:
```
Vasudev,480,1731622003112
John,350,1731622105320
```

---

## 🧩 Code Overview

### Main Components:
- `start()` → initialization & scene switching  
- `showMainMenu()` → difficulty selection  
- `startNewRound()` → prepares each round  
- `handleGuess()` → validates guess & updates state  
- Hint methods → Odd-even, Narrow-range, 50/50  
- Leaderboard read/write using NIO `Paths` & `Files`  
- UI built using JavaFX `VBox`, `HBox`, `Scene`, `Alerts`, etc.

---

## 📦 Technologies Used
- **Java 17+**
- **JavaFX 19+ / 21+ / 25+**
- **NIO File Handling**
- **OOP Architecture**
- **Interactive UI Components**

---

## 🙌 Author
**Vasudev Sharma**  
Oasis Infobyte Java Development Internship

---

## 📜 License
This project is released under the **MIT License**.

---

