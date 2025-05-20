[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/jYOfPNqO)

# 🧬 DNA Factory – Build Life, One Gene at a Time

**DNA Factory** is a grid-based factory automation game developed in Java with JavaFX. Step into the shoes of a rogue scientist and construct lifeforms from scratch using the basic units of biology—nucleotides. Extract raw materials, process gene sequences, synthesize proteins and traits, and ultimately build complex organisms like humans, octopuses, and worms.

Every step of life creation—whether it’s combining gene sequences, assembling organs, or fulfilling delivery quests—is wrapped in a satisfying, puzzle-like production loop. Designed with modular machines, tile-based logic, and a splash of synthetic biology, **DNA Factory** transforms complex science into a playful engineering challenge.

---

## 🎮 Features

- ⚙️ **Grid-Based World**: 2D tile map rendered with JavaFX Canvas
- 🧪 **Biological Crafting System**:
  - Extractors mine base nucleotides: **A**, **T**, **G**, **C**
  - **DNA Synthesizers** (multi-mode) convert nucleotides into gene sequences
  - **Ribosomes** process sequences into proteins or traits (Muscle, Blood, Brain)
  - **Organ Synthesizers** combine proteins + traits into organs
  - **Lifeform Assemblers** create living organisms from organs
- 📦 **Delivery Hub**: Accepts any crafted product for quests and progression
- 🛠️ **Modular Machines**: Each with directional logic and animated processing
- 🧠 **Puzzle-Like Gameplay**: Requires optimization of machine placement and flow
- 🧰 **Toolbar UI**: Select tools and build modes with real-time updates

---

## 🧠 Gameplay Loop

1. Extract base nucleotides from resource tiles.
2. Use conveyor belts to transport materials between machines.
3. Synthesize DNA sequences using mode-specific logic.
4. Convert sequences into traits or proteins with ribosomes.
5. Combine them into organs and finally assemble lifeforms.
6. Deliver finished products to the hub to complete missions and unlock new stages.

---

## 🧱 System Architecture

- **Tile Grid Engine**: All world elements (entities, items, resources) live on a 2D grid.
- **Entity Hierarchy**: All machines derive from a shared `Entity` class with polymorphic behavior.
- **Game Loop**: Real-time updates powered by JavaFX’s `AnimationTimer`.
- **GameState/GameRenderer Pattern**: Separates simulation logic and rendering visuals.
- **ItemMover System**: Custom engine to ensure correct item transfer and prevent logic collisions.
- **OrderManager**: Tracks delivery goals and handles quest progression.

---

## 🧩 Design Patterns in Use

- **Factory Pattern** – Create entities dynamically based on type
- **Observer Pattern** – Real-time HUD updates from game state changes
- **Strategy Pattern** – Switch behavior per machine mode (e.g. different ribosome modes)
- **Singleton Pattern** – Global managers like `Game`and `AssetManager`
- **OOP Best Practices** – Clean, modular, and extensible code

---

## 🕹 How to Play

| Step | Action |
|------|--------|
| 1. | Select a machine or item from the toolbar |
| 2. | Place it on the grid using the mouse |
| 3. | Rotate machines with `R` |
| 4. | Build conveyor paths to direct item flow |
| 5. | Route materials to machines in the correct order |
| 6. | Deliver outputs to the Delivery Hub to complete stages |

### 📘 Crafting Recipes

#### 🧪 DNA Synthesizer Modes (Gene Sequence Crafting)

| Mode   | Synthesizer Color | Required Inputs       | Output Sequence |
|--------|-------------------|------------------------|------------------|
| Mode 1 | Blue              | Nucleotides: A, T, G   | `ATGGGATAA`      |
| Mode 2 | Yellow            | Nucleotides: T, C, G   | `TCTCCGG`        |
| Mode 3 | Green             | Nucleotides: C, G, A   | `CCGAGA`         |

#### 🧬 Gene Sequences to Proteins/Traits

| Sequence     | Ribosome Mode | Output        |
|--------------|---------------|---------------|
| `ATGGGATAA`  | Mode 1        | Enzyme        |
| `TCTCCGG`    | Mode 1        | Antibodies    |
| `ATGGGATAA`  | Mode 2        | Power Trait   |
| `TCTCCGG`    | Mode 2        | Blood Trait   |
| `CCGAGA`     | Mode 2        | Intelligence Trait   |

#### 🧠 Traits + Proteins to Organs

| Input                        | Output        |
|-----------------------------|---------------|
| Enzyme + Intelligence Trait | Brain (Organ) |
| Antibodies + Blood Trait    | Heart         |
| Antibodies + Power Trait    | Lungs         |

#### 🧍 Organs to Lifeforms

| Input             | Output     |
|------------------|------------|
| Brain + Heart     | Human      |
| Brain + Lungs     | Octopus    |
| Heart + Lungs     | Worm       |

---

## 🎛 Controls

### 🔧 Basic Controls

| Action             | Key / Input                        |
|-------------------|-------------------------------------|
| Place machine      | Left Click on grid                  |
| Rotate machine     | `R`                                 |
| Delete machine     | `X`                                 |
| Move camera        | `W`, `A`, `S`, `D` or move mouse to screen edge |
| Zoom               | Mouse Scroll                        |

### 🏗️ Build Shortcuts & Machine Modes

| Machine / Action       | Key(s)                         |
|------------------------|--------------------------------|
| Conveyor               | `1`                            |
| Tunnel                 | `2`                            |
| Extractor              | `3`                            |
| DNA Synthesizer        | `4`, then `F1` / `F2` / `F3` to select mode |
| Ribosome               | `5`, then `F1` / `F2` to select mode |
| Organ Synthesizer      | `6`                            |
| Lifeform Assembler     | `7`                            |

---

## 🛠 Tech Stack

- **Java 17+**
- **JavaFX**
- **Scene Builder**
- **Eclipse** as IDE

## 🚀 How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/2110215-ProgMeth/cp-project-2024-2-himalayasalt.git
   cd DNAFactory
   
2. Open in your preferred Java IDE.
3. Ensure JavaFX is properly configured in your project/module settings.
4. Run Main.java to start the game.

## 🙌 Credits
Developed by [Theerapas Apinankul](https://github.com/theerapas) and [Thanakrit Weeraphatiwat](https://github.com/Champy2005) for an academic project. Fueled by curiosity and a love of synthetic biology and automation.
