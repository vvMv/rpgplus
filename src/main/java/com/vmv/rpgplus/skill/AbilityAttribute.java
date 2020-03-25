package com.vmv.rpgplus.skill;

public enum AbilityAttribute {

    /**
     * @identifier Must be the value of a config value within the ability
     */
    DECREASE_COOLDOWN("cooldown", "Decrease the ability cooldown"),
    INCREASE_DURATION("duration", "Increase the ability duration"),
    INCREASE_SPEED("speed", "Upgrade the speed value"),
    INCREASE_HEARTS("hearts", "Increase your number of hearts"),
    INCREASE_RANGE("range", "Increase the ability range"),
    INCREASE_ARROWS("arrows", "Increase the amount of arrows"),
    INCREASE_EXPLOSION("explosion", "Increase the explosion size"),
    DECREASE_SELFDAMAGE("selfdamage", "Decrease the damage taken"),
    INCREASE_LIFE_STEAL("life_steal", "Increase the percentage of damage healed"),
    INCREASE_LIFE_STEAL_CHANCE("life_steal_chance", "Increase the chance of life steal happening");

    String description;
    String identifier;

    AbilityAttribute(String identifier, String description) {
        this.description = description;
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getBaseValue(Ability ability) {
        return ability.getAbilityConfigSection().getDouble(this.identifier);
    }

    public double getValuePerPoint(Ability ability) {
        return ability.getAbilityConfigSection().getDouble("attributes." + this.toString().toLowerCase() + ".per_point");
    }

    public double getValueMaxPoint(Ability ability) {
        return ability.getAbilityConfigSection().getDouble("attributes." + this.toString().toLowerCase() + ".max_point");
    }

}
