# SeaBattle - Console Game

A console-based implementation of the classic board game, Battleship, developed in Kotlin. Test your strategy against an AI opponent in a game of logic, deduction, and pure guesswork.

## 🎨 Overview

The game features a simple text-based interface where you manage your fleet and hunt for the enemy's ships.

```text
   Computer (Player 2)         |           Player (Player 1)
---------------------------------|---------------------------------
   A B C D E F G H               |           A B C D E F G H
1  ? ? ? ? ? ? ? ?               |        1  ~ ~ ~ ~ ~ ~ ~ ~
2  ? ? ? ? ? ? ? ?               |        2  ~ 1 ~ ~ ~ ~ ~ ~
3  ? ? ? ? ? ? ? ?               |        3  ~ ~ ~ 3 3 3 ~ ~
4  ? ? X ? ? ? ? ?               |        4  ~ ~ ~ ~ ~ ~ ~ ~
5  ? ? ? ? ? ? ? ?               |        5  ~ ~ 4 4 4 4 ~ ~
6  ? ? ? ? ? ? ? ?               |        6  ~ ~ ~ ~ ~ ~ ~ ~
7  ? ? ? 2 ? ? ? ?               |        7  ~ 2 ~ ~ ~ ~ ~ ~
8  ? ? ? ? ? ? ? ?               |        8  ~ 2 ~ ~ ~ ~ ~ ~
```

## 🛠️ Key Features

  * **Dynamic Board Size:** Define your own battlefield (e.g., 8x8, 10x10).
  * **Strategic Placement:** Manually position your fleet on the board.
  * **Challenging AI:** Play against a computer opponent that auto-places its ships and hunts you down.
  * **Turn-Based Combat:** You and the computer take turns firing shots until one fleet is completely destroyed.
  * **Save/Load System:** Save your progress to a file and resume the battle later.

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

You need to have the following software installed:

  * **Java Development Kit (JDK)** (Version 8 or higher)
  * **Kotlin Compiler** (`kotlinc`)

### 🏃‍♂️ Running the Game

To set up and run the game locally, follow these steps:

#### 1\. Clone the Repository

Use Git to download the source code:

```bash
git clone https://github.com/nelsonalmeida2/SeaBattle.git
cd SeaBattle
```

#### 2\. Compile the Code

Use the Kotlin compiler (`kotlinc`) to build the executable JAR file:

```bash
kotlinc src/main.kt -include-runtime -d battleship.jar
```

#### 3\. Run the Application

Execute the compiled JAR file using the Java Runtime Environment (JRE):

```bash
java -jar battleship.jar
```

The game will start in your console, showing the **Main Menu**.

## 🎮 How to Play

The game is controlled through menus and coordinate-based input.

### Main Menu

You will interact with the game using the following options (in Portuguese):

1.  **Definir Tabuleiro e Navios (Define Board and Ships)**: Set the board size and place your fleet. The computer's ships are placed automatically.
2.  **Jogar (Play)**: Start the game. You and the computer will take turns firing shots.
3.  **Gravar (Save)**: Save the current game state to a file (e.g., `game.txt`).
4.  **Ler (Load)**: Load a saved game state from a file.
5.  **Sair (Exit)**: Quit the application.

### Understanding the Board

#### Coordinates

  * Coordinates are given as **Line, Column** (e.g., `6,G`).
  * Lines are numbers on the vertical axis (1, 2, 3, ...).
  * Columns are letters on the horizontal axis (A, B, C, ...).

#### Your Fleet (Your "Real" Board)

This board shows where you placed your ships.

| Symbol | Meaning | Dimension | Ship Name (English) | Ship Name (Portuguese) |
| :--- | :--- | :--- | :--- | :--- |
| `1` | Submarine | 1 | Submarine | Submarino |
| `2` | Destroyer | 2 | Destroyer | Contra-torpedeiro |
| `3` | Cruiser | 3 | Cruiser | Navio-tanque |
| `4` | Carrier | 4 | Carrier | Porta-aviões |

#### Your Shots (Your "Guess" Board)

This board shows the results of your shots against the opponent.

| Symbol | Meaning |
| :--- | :--- |
| `?` | Unknown (You have not fired here) |
| `X` | Water (Miss) |
| `1` | Hit on a Submarine |
| `2` | Hit on a Destroyer |
| `3` | Hit on a Cruiser |
| `4` | Hit on a Carrier |

#### Placement Directions

When setting up your board, you will use these directions to orient your ships:

  * **N**: North (Up)
  * **S**: South (Down)
  * **E**: East (Right)
  * **O**: West (Left)

### Special Inputs

In most input prompts, you can use:

  * **`-1`**: To return to the **Main Menu**.
  * **`0`**: To **Exit** the game immediately.

## 📂 Project Structure

The application's logic is contained within a single Kotlin file.

```
└── src
    └── main.kt  # Contains all game constants, functions, and the main game loop.
```

## ℹ️ Project Information

  * **Developed by**: Nelson Almeida
  * **Version**: 1.0
  * **License**: MIT

-----

Would you like me to suggest some improvements to the Kotlin code itself or help you add new features?
