# Tile Matching Game Environment

A flexible Java-based environment for creating and playing various tile matching games like Tetris and Bejeweled.

## Overview

This project provides a modular framework for developing tile matching games. The environment is designed to be extensible, allowing for the implementation of different game mechanics while reusing common components.

## Features

- Flexible grid-based game environment
- Support for different game types (Tetris, Bejeweled, etc.)
- Customizable block types and colors
- Score tracking and game state management
- Game launcher for selecting different games

## Included Games

### Tetris

The classic block-stacking game where you arrange falling tetrominoes to create complete rows.

- Controls:
  - Left/Right Arrow: Move tetromino left/right
  - Down Arrow: Move tetromino down
  - Up Arrow: Rotate tetromino
  - Space: Hard drop

### Bejeweled

A match-3 game where you swap adjacent gems to create matches of three or more.

- Controls:
  - Select a gem by pressing a key corresponding to its position
  - Select an adjacent gem to swap with the selected gem
  - WASD or Arrow keys can be used for navigation

## Architecture

The environment consists of several key components:

- **Display**: Handles the graphical representation of the game
- **GameState**: Manages the game logic and state
- **Timer**: Handles timing-related functionality
- **Player**: Tracks player statistics
- **Grid**: Manages the game grid and block placement
- **Block**: Represents individual blocks/gems in the game

## Extending the Framework

To create a new game type:

1. Create a new class that extends `GameState`
2. Implement the required abstract methods:
   - `handleInput(String input)`: Handle player input
   - `updateGame(double deltaTime)`: Update the game state
   - `renderUI(Graphics g)`: Render the user interface
   - `checkGameOver()`: Check if the game is over

## Running the Games

1. Compile the project
2. Run the `GameLauncher` class
3. Select a game from the launcher menu

## Requirements

- Java 17 or higher
- Swing library (included in standard JDK)

## Future Improvements

- Add more game types (Columns, Puzzle Bobble, etc.)
- Implement high score tracking
- Add sound effects and music
- Create more advanced visual effects
- Add multiplayer support
