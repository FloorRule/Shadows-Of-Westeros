# Shadows-Of-Westeros

**Shadows-Of-Westeros** is a command-line Dungeons & Dragons–style game set in the world of Game of Thrones.
The player chooses a hero and battles through multiple dungeon levels filled with enemies, traps, and obstacles.
The objective is to survive all levels, defeat all enemies, and reach the end of the dungeon.

This project follows object-oriented design principles and implements all required mechanics from the assignment specification.

---

## Overview

This is a single-player, multi-level, turn-based dungeon crawler.
Each level is a text-based board loaded from disk.
During each game tick:

1. The player performs one action.
2. Every enemy performs one action.

A level ends when all enemies are defeated.
The game ends when the player dies or when all levels are completed.

---

## How To Run

The game receives a directory path as a command-line argument:

```
java -jar {projectName}.jar <path_to_levels_folder>
```

Each level file contains:

* Walls (`#`)
* Empty spaces (`.`)
* Player (`@`)
* Enemies (any other character)

---


## Game Description

The game board is represented as a 2D grid of ASCII characters.
Each tile may be:

* Wall (`#`)
* Empty space (`.`)
* Player (`@`)
* Dead player (`X`)
* Various enemies (characters such as `s`, `k`, `M`, `B`, etc.)

The player moves through the board, fights enemies, and uses abilities depending on their class.

---

## Game Board

Boards are loaded from files named:

```
level1.txt  
level2.txt  
...
```

Each file represents a single level.
The board is parsed into tile objects with a character symbol and position.

---

## Game Tiles

Tiles are divided into:

* Empty
* Wall
* Unit (Player or Enemy)

Each unit has:

* Name
* Health pool
* Current health
* Attack
* Defense

Distance is calculated using Euclidean distance.

---

## Player

The player has the following additional attributes:

* Experience
* Player level
* Special class ability

### Leveling Up

When the player accumulates at least `50 × currentLevel` experience:

* experience decreases by `50 × level`
* level increases by 1
* health pool increases by `10 × level`
* current health is restored to full
* attack increases by `4 × level`
* defense increases by `1 × level`

Each class has additional level-up effects.

---

## Player Types

The game includes three main player classes, represented by Game of Thrones characters.

### Warrior

Special ability:
Avenger’s Shield – Hits a random enemy within range < 3 for 10% of max health and heals the warrior.

Unique properties:

* Ability cooldown
* Remaining cooldown

Warriors include:

* Jon Snow
* The Hound

### Mage

Special ability:
Blizzard – Hits random enemies within ability range for damage equal to spell power, consuming mana.

Properties include:

* Mana pool
* Current mana
* Mana cost
* Spell power
* Hit count
* Ability range

Mages include:

* Melisandre
* Thoros of Myr

### Rogue

Special ability:
Fan of Knives – Hits all enemies in range < 2 for damage equal to the rogue’s attack, consuming energy.

Rogues include:

* Arya Stark
* Bronn

---

## Enemies

Enemies are divided into two categories: Monsters and Traps.

### Monsters

Monsters can move, chase the player, and perform attacks.

Examples (Game of Thrones characters and creatures):

* Lannister Soldier (s)
* Lannister Knight (k)
* Queen’s Guard (q)
* Wight (z)
* Bear-Wright (b)
* Giant-Wright (g)
* White Walker (w)
* The Mountain (M)
* Queen Cersei (C)
* Night’s King (K)

Each monster has:

* Vision range
* Experience value

### Traps

Traps do not move, but have:

* Visibility time
* Invisibility time
* Visibility state
* Attack range

Trap types include:

* Bonus Trap (B)
* Queen’s Trap (Q)
* Death Trap (D)

---

## Combat System

Combat occurs when:

* The player steps onto an enemy tile, or
* An enemy steps onto the player

Combat flow:

1. Attacker rolls a value between 0 and attack points.
2. Defender rolls a value between 0 and defense points.
3. If attack roll minus defense roll is positive, defender takes that damage.
4. If the defender dies:

   * If it was an enemy: player gains XP and moves into the tile
   * If it was the player: tile becomes `X` and the game ends

---

## Units Implementation Guidelines

The game uses the Visitor Pattern for interactions between:

* Unit and empty tile
* Unit and wall
* Player and enemy

No `instanceof` or casting should be used.
Each unit implements:

* `toString()` – returns tile character
* `getName()` – used in combat messages
* `description()` – prints full stats

---

## Command Line Interface (CLI)

The CLI updates every game tick and prints:

* Full board
* Player stats
* Ability results
* Combat logs
* Level-up notifications

### Player Controls

| Key | Action       |
| --- | ------------ |
| w   | Move up      |
| s   | Move down    |
| a   | Move left    |
| d   | Move right   |
| e   | Cast ability |
| q   | Do nothing   |

---

## Testing

Unit testing is required for:

* Player movement
* Enemy movement
* Combat logic
* Traps
* Ability systems
* Edge cases (low HP, boundaries, overlapping actions)

---

## Bonus Features Included (Optional)

### Additional Player Class: Hunter

Special ability: Shoot – hits the closest enemy for full attack damage using arrows.

Hunter included:

* Ygritte

### Additional Enemy Class: Boss

Bosses behave like monsters but also cast abilities based on frequency timers.

Bosses include:

* The Mountain
* Queen Cersei
* Night’s King
