package com.narxoz.rpg;

import com.narxoz.rpg.artifact.Armor;
import com.narxoz.rpg.artifact.Inventory;
import com.narxoz.rpg.artifact.Potion;
import com.narxoz.rpg.artifact.Ring;
import com.narxoz.rpg.artifact.Scroll;
import com.narxoz.rpg.artifact.Weapon;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.vault.ChronomancerEngine;
import com.narxoz.rpg.vault.VaultRunResult;

import java.util.List;

/**
 * Entry point for Homework 9 — Chronomancer's Vault: Visitor + Memento.
 *
 * The demo sequence:
 *   1. Build two heroes with different starting states.
 *   2. Build a mixed vault inventory of 6 artifacts.
 *   3. Hand both off to ChronomancerEngine, which:
 *        - runs three visitors over the inventory,
 *        - saves a memento per hero,
 *        - applies a vault trap,
 *        - rewinds each hero from its memento,
 *        - runs a fourth visitor (open/closed proof).
 *   4. Print the final VaultRunResult.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Homework 9 Demo: Visitor + Memento ===");

        // 1. Build two heroes with different starting states.
        Hero arden = new Hero(
                "Arden the Bold",   // name
                100,                // hp
                30,                 // mana
                18,                 // attackPower
                10,                 // defense
                75,                 // gold
                new Inventory()
        );
        Hero lyra = new Hero(
                "Lyra Stormcaller",
                80,                 // hp
                60,                 // mana
                14,                 // attackPower
                7,                  // defense
                40,                 // gold
                new Inventory()
        );

        System.out.println("\n-- Initial Party --");
        System.out.println("  " + arden);
        System.out.println("  " + lyra);

        // 2. Build a mixed vault inventory with at least 5 artifacts.
        Inventory vaultInventory = new Inventory();
        vaultInventory.addArtifact(new Weapon("Crystal Greatsword",  220, 15, 9));
        vaultInventory.addArtifact(new Weapon("Bloodthirst Dagger",  150, 3, 14));   // CurseDetector should flag this
        vaultInventory.addArtifact(new Potion("Elixir of Vitality",  60, 1, 35));
        vaultInventory.addArtifact(new Potion("Unknown Vial",        20, 1, 0));     // CurseDetector should flag this
        vaultInventory.addArtifact(new Scroll("Scroll of Doomspeak", 90, 1, "Doom Echo")); // CurseDetector should flag
        vaultInventory.addArtifact(new Ring("Ring of Insight",       180, 1, 6));
        vaultInventory.addArtifact(new Armor("Runed Plate",          300, 25, 12));

        System.out.println("\n-- Vault Inventory (" + vaultInventory.size() + " items) --");
        vaultInventory.getArtifacts().forEach(a ->
                System.out.println("  * " + a.getName() + " (value=" + a.getValue()
                        + ", weight=" + a.getWeight() + ")"));

        // 3. Run the engine (visitor sweep + memento workflow + open/closed proof).
        ChronomancerEngine engine = new ChronomancerEngine(vaultInventory);
        VaultRunResult result = engine.runVault(List.of(arden, lyra));

        // 4. Final hero states + summary.
        System.out.println("-- Final Party (after vault run) --");
        System.out.println("  " + arden);
        System.out.println("  " + lyra);

        System.out.println("\n=== " + result + " ===");
    }
}
