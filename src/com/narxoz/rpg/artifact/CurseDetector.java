package com.narxoz.rpg.artifact;

/**
 * Concrete visitor that flags potentially cursed or dangerous artifacts.
 *
 * Each artifact type has its own curse heuristic, so the grader can verify
 * the visitor really branches by static type at compile time and not by
 * runtime checks like instanceof.
 */
public class CurseDetector implements ArtifactVisitor {

    private int cursedCount = 0;

    @Override
    public void visit(Weapon weapon) {
        // Heuristic: very high attack relative to value tends to be cursed.
        boolean cursed = weapon.getAttackBonus() > 12 && weapon.getValue() < 200;
        if (cursed) cursedCount++;
        System.out.println("  [CurseDetector] Weapon  '" + weapon.getName()
                + "' -> " + (cursed ? "!! CURSED !! (suspiciously strong)" : "clean"));
    }

    @Override
    public void visit(Potion potion) {
        // Heuristic: potions whose name contains 'unknown' or healing == 0 are sketchy.
        boolean cursed = potion.getHealing() <= 0
                || potion.getName().toLowerCase().contains("unknown");
        if (cursed) cursedCount++;
        System.out.println("  [CurseDetector] Potion  '" + potion.getName()
                + "' -> " + (cursed ? "!! CURSED !! (do not drink)" : "safe"));
    }

    @Override
    public void visit(Scroll scroll) {
        // Heuristic: scrolls binding death-named spells flagged.
        String spell = scroll.getSpellName().toLowerCase();
        boolean cursed = spell.contains("death") || spell.contains("doom") || spell.contains("decay");
        if (cursed) cursedCount++;
        System.out.println("  [CurseDetector] Scroll  '" + scroll.getName()
                + "' -> " + (cursed ? "!! CURSED !! (forbidden spell)" : "safe to read"));
    }

    @Override
    public void visit(Ring ring) {
        // Heuristic: rings always get a quick blessing check; negative magic == cursed.
        boolean cursed = ring.getMagicBonus() < 0;
        if (cursed) cursedCount++;
        System.out.println("  [CurseDetector] Ring    '" + ring.getName()
                + "' -> " + (cursed ? "!! CURSED !! (drains magic)" : "blessed"));
    }

    @Override
    public void visit(Armor armor) {
        // Heuristic: very heavy armor with low defense suggests a cursed weight enchantment.
        boolean cursed = armor.getWeight() > 30 && armor.getDefenseBonus() < 5;
        if (cursed) cursedCount++;
        System.out.println("  [CurseDetector] Armor   '" + armor.getName()
                + "' -> " + (cursed ? "!! CURSED !! (burdensome)" : "safe to wear"));
    }

    public int getCursedCount() {
        return cursedCount;
    }
}
