package com.narxoz.rpg.vault;

import com.narxoz.rpg.artifact.CurseDetector;
import com.narxoz.rpg.artifact.EnchantmentScanner;
import com.narxoz.rpg.artifact.GoldAppraiser;
import com.narxoz.rpg.artifact.Inventory;
import com.narxoz.rpg.artifact.WeightCalculator;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.combatant.HeroMemento;
import com.narxoz.rpg.memento.Caretaker;

import java.util.List;

/**
 * Orchestrates the Chronomancer's Vault demo run.
 *
 * The engine ties together:
 *   - the visitor sweep over a shared vault inventory
 *   - per-hero memento save/restore around a vault trap
 *   - a final VaultRunResult summary
 *
 * It deliberately stays free of any instanceof or runtime-type checks; all
 * artifact-type branching happens inside the visitor classes.
 */
public class ChronomancerEngine {

    private final Inventory vaultInventory;

    public ChronomancerEngine(Inventory vaultInventory) {
        this.vaultInventory = vaultInventory == null ? new Inventory() : vaultInventory;
    }

    /**
     * Runs the vault sequence for the supplied party.
     *
     * @param party the heroes entering the vault
     * @return a summary of the run
     */
    public VaultRunResult runVault(List<Hero> party) {
        if (party == null || party.isEmpty()) {
            System.out.println(">> Empty party — nothing to run.");
            return new VaultRunResult(0, 0, 0);
        }

        // ---------------------- Phase 1: Visitor sweep ----------------------
        System.out.println("\n>>> Phase 1: Vault Inventory Appraisal (Visitor sweep)");
        System.out.println("    Vault holds " + vaultInventory.size() + " artifacts.\n");

        System.out.println("--- 1a. GoldAppraiser pass ---");
        GoldAppraiser appraiser = new GoldAppraiser();
        vaultInventory.accept(appraiser);
        System.out.println("    => Total resale value: " + appraiser.getTotalGold() + " gold\n");

        System.out.println("--- 1b. EnchantmentScanner pass ---");
        EnchantmentScanner scanner = new EnchantmentScanner();
        vaultInventory.accept(scanner);
        System.out.println("    => Magical items detected: " + scanner.getMagicalCount() + "\n");

        System.out.println("--- 1c. CurseDetector pass ---");
        CurseDetector detector = new CurseDetector();
        vaultInventory.accept(detector);
        System.out.println("    => Cursed/dangerous items: " + detector.getCursedCount() + "\n");

        // ---------------------- Phase 2: Memento workflow ----------------------
        System.out.println(">>> Phase 2: Memento Workflow (Save -> Trap -> Restore)\n");

        Caretaker caretaker = new Caretaker();
        int mementosCreated = 0;
        int restoredCount = 0;

        for (Hero hero : party) {
            System.out.println("--- Hero: " + hero.getName() + " ---");
            System.out.println("    BEFORE save: " + hero);

            // Save snapshot
            HeroMemento snapshot = hero.createMemento();
            caretaker.save(snapshot);
            mementosCreated++;
            System.out.println("    [snapshot saved] caretaker history size = " + caretaker.size());

            // Vault trap simulation: damage + mana drain + lose gold
            System.out.println("    *** A chronomantic trap detonates! ***");
            hero.takeDamage(40);
            hero.spendMana(Math.min(20, hero.getMana()));
            hero.spendGold(Math.min(15, hero.getGold()));
            System.out.println("    AFTER trap: " + hero);

            // Rewind via memento
            HeroMemento restoreSnap = caretaker.undo();
            if (restoreSnap != null) {
                hero.restoreFromMemento(restoreSnap);
                restoredCount++;
                System.out.println("    [rewound from memento] caretaker history size = " + caretaker.size());
                System.out.println("    AFTER rewind: " + hero);
            }
            System.out.println();
        }

        // ---------------------- Phase 3 (Part 4): Open/closed proof ----------
        System.out.println(">>> Phase 3: Open/Closed Proof — 4th visitor added without changing artifact/");
        System.out.println("--- WeightCalculator pass ---");
        WeightCalculator weigher = new WeightCalculator();
        vaultInventory.accept(weigher);
        System.out.println("    => Raw weight: " + weigher.getRawWeight()
                + ", encumbrance score: " + weigher.getEncumbranceScore() + "\n");

        return new VaultRunResult(vaultInventory.size(), mementosCreated, restoredCount);
    }
}
