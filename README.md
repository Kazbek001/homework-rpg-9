# Homework 9 — Chronomancer's Vault: Visitor + Memento

RPG-style demo of two behavioral design patterns working side by side:

- **Visitor** — appraises a heterogeneous artifact inventory through double dispatch
- **Memento** — saves and restores a hero's mutable state without exposing internals

---

## Project Structure

```
src/com/narxoz/rpg/
├── Main.java                       # Entry point — runs the full demo
├── artifact/
│   ├── Artifact.java               # Abstract base class
│   ├── ArtifactVisitor.java        # Visitor interface (5 visit overloads)
│   ├── Weapon.java                 # Concrete artifact
│   ├── Potion.java                 # Concrete artifact
│   ├── Scroll.java                 # Concrete artifact
│   ├── Ring.java                   # Concrete artifact
│   ├── Armor.java                  # Concrete artifact
│   ├── Inventory.java              # Holds artifacts, drives traversal
│   ├── GoldAppraiser.java          # Visitor #1 — resale value
│   ├── EnchantmentScanner.java     # Visitor #2 — magic properties
│   ├── CurseDetector.java          # Visitor #3 — danger flags
│   └── WeightCalculator.java       # Visitor #4 — open/closed proof
├── combatant/
│   ├── Hero.java                   # Originator
│   └── HeroMemento.java            # Memento (package-private getters)
├── memento/
│   └── Caretaker.java              # Stores opaque snapshots
└── vault/
    ├── ChronomancerEngine.java     # Orchestrates the demo
    └── VaultRunResult.java         # Run summary
```

---

## How to Run

### From IntelliJ IDEA
1. Open the project folder.
2. Right-click `Main.java` → **Run 'Main.main()'**.

### From the command line
```bash
javac -d out $(find src -name "*.java")
java -cp out com.narxoz.rpg.Main
```

On Windows (cmd):
```cmd
javac -d out src\com\narxoz\rpg\*.java src\com\narxoz\rpg\artifact\*.java src\com\narxoz\rpg\combatant\*.java src\com\narxoz\rpg\memento\*.java src\com\narxoz\rpg\vault\*.java
java -cp out com.narxoz.rpg.Main
```

Requires **Java 17 or newer** (uses `List.of()` / `List.copyOf()`).

---

## Pattern #1 — Visitor

**Goal:** apply different reports to a mixed artifact inventory without `instanceof` chains.

- `ArtifactVisitor` defines five `visit(...)` overloads — one per artifact type.
- Each artifact's `accept(visitor)` is the **only** place that calls `visitor.visit(this)`. This is the double-dispatch pivot.
- `Inventory.accept(visitor)` walks the list and forwards each artifact's `accept`.

**Concrete visitors:**

| Visitor | What it does |
|---|---|
| `GoldAppraiser` | Computes resale value; weapons/rings get markup, potions get a discount |
| `EnchantmentScanner` | Prints magical aura per artifact type |
| `CurseDetector` | Flags dangerous artifacts using per-type heuristics |
| `WeightCalculator` | (Part 4) sums encumbrance with per-type modifiers — added without changing any `artifact/*.java` |

**No `instanceof` is used anywhere.** All branching happens through the visitor's overload resolution.

---

## Pattern #2 — Memento

**Goal:** snapshot the hero's mutable state, change it, then rewind cleanly.

- `Hero` is the **Originator**. It exposes only:
  - `createMemento(): HeroMemento`
  - `restoreFromMemento(HeroMemento)`
- `HeroMemento` is the **Memento**. It lives in `combatant/` next to `Hero` so the originator can read it. Its getters are **package-private** — outside code cannot inspect the saved state.
- `Caretaker` is the **Caretaker**, in a separate `memento/` package. It only knows `save / undo / peek / size` — it never reads memento fields.

The hero captures: `hp`, `mana`, `gold`, `maxHp`, `attackPower`, `defense`, and `inventory`.

---

## Demo Flow (`ChronomancerEngine.runVault`)

1. **Phase 1 — Visitor sweep.** Three visitors traverse the vault inventory and print per-artifact reports.
2. **Phase 2 — Memento workflow.** For each hero:
   - Save a snapshot.
   - A chronomantic trap deals damage, drains mana, steals gold.
   - Rewind via the saved memento — hero state matches the original exactly.
3. **Phase 3 — Open/Closed proof.** A 4th visitor (`WeightCalculator`) is run without any change in `artifact/`.

The console output clearly labels each phase, each visitor, and the BEFORE / AFTER trap / AFTER rewind states for both heroes. The run ends with a printed `VaultRunResult`.

---

## Sample Output (truncated)

```
=== Homework 9 Demo: Visitor + Memento ===

-- Initial Party --
  Hero{name='Arden the Bold', hp=100, mana=30, gold=75, ...}
  Hero{name='Lyra Stormcaller', hp=80, mana=60, gold=40, ...}

>>> Phase 1: Vault Inventory Appraisal (Visitor sweep)
--- 1a. GoldAppraiser pass ---
  [GoldAppraiser] Weapon  'Crystal Greatsword' -> resale 282 gold
  ...
    => Total resale value: 1288 gold

>>> Phase 2: Memento Workflow (Save -> Trap -> Restore)
--- Hero: Arden the Bold ---
    BEFORE save: hp=100, mana=30, gold=75
    *** A chronomantic trap detonates! ***
    AFTER trap:  hp=60,  mana=10, gold=60
    AFTER rewind: hp=100, mana=30, gold=75

>>> Phase 3: Open/Closed Proof — 4th visitor added without changing artifact/
  ...

=== VaultRunResult{artifactsAppraised=7, mementosCreated=2, restoredCount=2} ===
```

---

## Diagrams

Two UML diagrams are included:

- **`Diagram_1_Visitor.png`** — `ArtifactVisitor`, all artifact classes, `Inventory`, all four concrete visitors
- **`Diagram_2_Memento.png`** — `Hero`, `HeroMemento`, `Caretaker`, `ChronomancerEngine`

---

## Pattern Boundaries — Why They Stay Independent

- The vault can run **any** subset of visitors without touching the hero.
- The hero can save/restore mementos **without ever invoking a visitor**.
- The two patterns intersect only inside `ChronomancerEngine`, which simply schedules them.

This proves the patterns are structurally independent even though they appear in the same demo.
