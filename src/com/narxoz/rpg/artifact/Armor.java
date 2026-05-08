package com.narxoz.rpg.artifact;

/**
 * A protective suit, plate, or enchanted shield.
 */
public class Armor extends Artifact {

    private final int defenseBonus;

    public Armor(String name, int value, int weight, int defenseBonus) {
        super(name, value, weight);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    @Override
    public void accept(ArtifactVisitor visitor) {
        visitor.visit(this);
    }
}
