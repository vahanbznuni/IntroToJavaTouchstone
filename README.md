# CLI Inventory Manager (Java) — Student Project

![Java](https://img.shields.io/badge/Java-11%2B-007396?logo=openjdk&logoColor=white)

A simple, portfolio-friendly command‑line app to **browse, add, edit, and delete inventory items** stored in a CSV file. Built with clean OOP design, robust input handling, and a clear separation between data model and CLI orchestration.

---

## Demo

> 🎥 Live demo (OnlineGDB): https://onlinegdb.com/AOGmlgaZZ
  - Console-only app; use the menus to explore, add, update, and delete items.

---

## Features

- **Menu-driven CLI**
  - Main → Department → Subcategory → Item workflow
  - Actions: *Update Name*, *Update Description*, *Delete Item* (with confirmation)
- **CRUD on inventory items** (name + description for each item)
- **CSV persistence**
  - Loads on startup; **saves on exit**
  - Creates `data_bak.csv` backup before overwrite
- **Graceful input handling**
  - Rejects non-integer menu input and out‑of‑range choices
  - Flushes scanner to avoid infinite loops after invalid input
- **Defensive error handling**
  - `DuplicateKeyException` for duplicate items
  - `CorruptDataException` for malformed CSV rows
- **Data hygiene**
  - Auto‑prunes empty subcategories and departments after deletions
- **Clear, commented codebase** suitable for learning & review

---

## Tech Stack

- **Language:** Java (standard library only)
- **Runtime:** Works with a recent JDK (e.g., 11+)
- **I/O:** CSV file persisted on disk

---

## Architecture

- `Item` — simple data object (name, description, `toString()`)
- `Inventory` — nested map structure: `Map<Department, Map<Subcategory, Map<ItemName, Item>>>`
  - `loadFromCSV(...)`, `saveData(...)`, `addItem(...)`, `getItem(...)`, `deleteItem(...)`, `hasItem(...)`
- `InventoryManager` — CLI driver / orchestrator
  - Menus, input validation, and program flow
- `DuplicateKeyException`, `CorruptDataException` — domain-specific exceptions

---

## Project Structure

```
.
├── Item.java
├── Inventory.java
├── InventoryManager.java
├── DuplicateKeyException.java
├── CorruptDataException.java
└── data.csv
```

---

## Getting Started

### Prerequisites
- JDK (11 or later recommended)

### Run locally
```bash
# Compile
javac *.java

# Run (expects data.csv in the same folder)
java InventoryManager
```

### Sample `data.csv`
> **Important**: Place `data.csv` alongside the compiled classes. The program expects **four columns** per row in this order.

**Header row (required):**
```
Department, SubCategory, Item Name, Item Description
```

**Example rows:**
```
Electronics, Laptops, SwiftBook Pro 16, "Lightweight laptop with powerful specs for professionals"
Garden and Outdoors, Grills and BBQ, Silver Browman Grill, "Classic outdoor grill"
```

> On save, the app writes rows and keeps a backup as `data_bak.csv`.

---

## Usage

- **Main Menu**
  1. Browse Inventory → pick Department → Subcategory → Item
     - Then choose: *Update Name*, *Update Description*, *Delete Item*, or *Main Menu*
  2. Add a New Item → enter Department, Subcategory, Item Name, Description
  3. Quit → Saves CSV (and makes a backup) then exits

---

## Error Handling & Validation

- **Input**: Non-integer input and out‑of‑range selections are caught and the user is re‑prompted.
- **Duplicates**: Adding an item with an existing name in the same subcategory triggers `DuplicateKeyException`.
- **Corrupt data**: Rows that don’t have exactly four columns trigger `CorruptDataException` during load.




