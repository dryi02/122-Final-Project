# Tile Matching Game Environment

A flexible Java-based environment for creating and playing match-3 style tile matching games.

Created By: Armon Amini, Daniel Robert Yi, Justin Chan, Susannah Liu, and Matthew Duong

Documentation (UCI Emails Only): https://docs.google.com/document/d/1vad8RvlvUfk1nLkANLdCQICH2ElEYcd8d_VfBfBbcE4/
  
## Overview

This project provides a modular framework for developing tile matching games. The environment includes two different games: Bejeweled and SameGame, each with unique mechanics and gameplay styles.

## Features

- Flexible grid-based game environment
- Interactive block manipulation and matching
- Customizable block types and colors
- Score tracking and game state management
- Gravity effects and cascading matches
- Player statistics tracking across games
- Menu system for game selection

## Games

### Bejeweled

A time-based match-3 game where players take turns making matches.

#### Features

- **Time Limit**: Each player has 30 seconds per turn
- **Global Timer**: 5-minute game time limit
- **Block Swapping**: Swap adjacent blocks to create matches
- **Gravity Effects**: Blocks fall to fill empty spaces
- **Score Tracking**: Points for each match made
- **Win Tracking**: Keeps track of wins across games

#### Controls

- **Arrow Keys**: Move selection cursor
- **Space**: Select/Confirm block swap
- **M**: Return to menu
- **ESC**: Exit game

### SameGame

A turn-based game where players take turns removing connected blocks.

#### Features

- **Turn-Based**: Players alternate turns
- **Connected Block Removal**: Remove groups of same-colored blocks
- **Grid Reset**: Grid resets after each player's turn
- **Win Tracking**: Keeps track of wins across games
- **Least Turns Wins**: Player with fewer turns wins

#### Controls

- **Arrow Keys**: Move selection cursor
- **P**: Pop connected blocks
- **M**: Return to menu
- **ESC**: Exit game

## Architecture

The environment consists of several key components:

- **GameChooser**: Main menu system for game selection and player management
- **Display**: Handles the graphical representation of the game
- **GameState**: Abstract base class for game logic and state
- **Grid**: Manages the game grid and block placement
- **Block**: Represents individual blocks in the game
- **Player**: Manages player information and scores

## Running the Game

Method 1:
   1. Run the TMGE.jar file


Method 2:
   1. Compile the project:

      ```
      javac -d bin src/main/java/tilematch/*.java
      ```

   2. Run the game:
      ```
      java -cp bin tilematch.GameChooser
      ```

