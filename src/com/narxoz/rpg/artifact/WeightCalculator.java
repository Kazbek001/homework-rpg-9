package com.narxoz.rpg.artifact;

/**
 * 4th concrete visitor — Part 4 open/closed proof.
 *
 * This visitor was added AFTER the first three were already working. Note
 * that no file under artifact/ has been modified to add a new report — the
 * artifact hierarchy stays closed for modification, open for extension.
 *
 * Sums total carry weight and applies a per-type encumbrance modifier.
 */
public class WeightCalculator implements ArtifactVisitor {

    private int rawWeight = 0;
    private double encumbranceScore = 0.0;

    @Override
    public void visit(Weapon weapon) {
        rawWeight += weapon.getWeight();
        // Weapons swing freely — full weight contributes to encumbrance.
        encumbranceScore += weapon.getWeight() * 1.0;
        System.out.println("  [WeightCalc]    Weapon  '" + weapon.getName()
                + "' weight=" + weapon.getWeight() + "  (full encumbrance)");
    }

    @Override
    public void visit(Potion potion) {
        rawWeight += potion.getWeight();
        // Potions ride in a belt pouch, only half encumbrance.
        encumbranceScore += potion.getWeight() * 0.5;
        System.out.println("  [WeightCalc]    Potion  '" + potion.getName()
                + "' weight=" + potion.getWeight() + "  (belt pouch x0.5)");
    }

    @Override
    public void visit(Scroll scroll) {
        rawWeight += scroll.getWeight();
        // Scrolls are nearly weightless in carry terms.
        encumbranceScore += scroll.getWeight() * 0.25;
        System.out.println("  [WeightCalc]    Scroll  '" + scroll.getName()
                + "' weight=" + scroll.getWeight() + "  (parchment x0.25)");
    }

    @Override
    public void visit(Ring ring) {
        rawWeight += ring.getWeight();
        // Rings are worn — they contribute 0 to encumbrance.
        encumbranceScore += 0;
        System.out.println("  [WeightCalc]    Ring    '" + ring.getName()
                + "' weight=" + ring.getWeight() + "  (worn x0)");
    }

    @Override
    public void visit(Armor armor) {
        rawWeight += armor.getWeight();
        // Armor doubles in encumbrance because it must be worn AND carried until donned.
        encumbranceScore += armor.getWeight() * 2.0;
        System.out.println("  [WeightCalc]    Armor   '" + armor.getName()
                + "' weight=" + armor.getWeight() + "  (heavy gear x2)");
    }

    public int getRawWeight() {
        return rawWeight;
    }

    public double getEncumbranceScore() {
        return encumbranceScore;
    }
}
