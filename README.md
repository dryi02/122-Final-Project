# Tile Matching Game Environment

A flexible Java-based environment for creating and playing match-3 style tile matching games.

## Overview

This project provides a modular framework for developing tile matching games. The environment is designed to be extensible, allowing for the implementation of different game mechanics while reusing common components.

## Features

- Flexible grid-based game environment
- Interactive block manipulation and matching
- Customizable block types and colors
- Score tracking and game state management
- Gravity effects and cascading matches

## Grid Demo Game

The project includes a fully functional match-3 style game demo that demonstrates the core mechanics of tile matching games:

### Features

- **Block Swapping**: Swap adjacent blocks to create matches of 3 or more
- **Connected Block Popping**: Remove groups of 3+ connected blocks of the same color
- **Gravity Effects**: Blocks fall to fill empty spaces after matches are removed
- **Cascading Matches**: Automatic detection and removal of new matches after blocks fall
- **Randomization Options**: Generate grids with or without initial matches
- **Score Tracking**: Earn points for each block removed

### Controls

- **Arrow Keys**: Move selection cursor
- **Space**: Toggle block/Confirm swap
- **S**: Toggle swap mode (for swapping blocks)
- **P**: Pop connected blocks (3+ of same color)
- **A**: Check all matches on the board
- **M**: Toggle initial matches option (for randomization)
- **R**: Randomize grid
- **C**: Clear grid
- **Escape**: Exit the demo

### How to Play

1. **Basic Gameplay**:

   - Use arrow keys to move the selection cursor
   - Press 'S' to enter swap mode
   - Select a block, then move to an adjacent block and press Space to swap
   - If the swap creates a match of 3 or more blocks, they will be removed
   - Blocks will fall to fill empty spaces, and new blocks will appear at the top
   - Chain reactions can occur if falling blocks create new matches

2. **Manual Popping**:

   - Select a block that is part of a group of 3 or more connected blocks of the same color
   - Press 'P' to pop all connected blocks of that color
   - Score points based on the number of blocks removed

3. **Randomization Options**:
   - Press 'M' to toggle whether initial matches are allowed when randomizing
   - Press 'R' to randomize the grid according to your preference
   - "Initial Matches: Allowed" creates a completely random grid
   - "Initial Matches: Not Allowed" ensures no matches exist when the grid is created

## Architecture

The environment consists of several key components:

- **Display**: Handles the graphical representation of the game
- **GameState**: Manages the game logic and state
- **Grid**: Manages the game grid and block placement
- **Block**: Represents individual blocks in the game
- **GridDemoState**: Implements the match-3 game mechanics
- **GridDemoLauncher**: Handles the game loop and input processing

## Running the Game

1. Compile the project:

   ```
   javac -d bin src/main/java/tilematch/*.java
   ```

2. Run the game:
   ```
   java -cp bin tilematch.Main
   ```

## Requirements

- Java 8 or higher
- Swing library (included in standard JDK)

## Future Improvements

- Add more block types with special abilities
- Implement level progression
- Add time-based challenges
- Create more advanced visual effects
- Add sound effects and music
