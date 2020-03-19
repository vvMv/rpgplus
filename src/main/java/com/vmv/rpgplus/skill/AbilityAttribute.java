package com.vmv.rpgplus.skill;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;

public enum AbilityAttribute {

    /**
     * @identifier Must be the value of a config value within the ability
     */
    DECREASE_COOLDOWN("cooldown", "This will decrease the ability cooldown"),
    INCREASE_DURATION("duration", "This will increase the ability duration"),
    INCREASE_SPEED("speed", "This will upgrade the speed value"),
    INCREASE_HEARTS("hearts", "This will increase your number of hearts"),
    INCREASE_RANGE("range", "This will increase the ability range"),
    INCREASE_ARROWS("arrows", "This will increase the amount of arrows"),
    INCREASE_EXPLOSION("explosion", "This will increase the explosion size"),
    DECREASE_SELFDAMAGE("selfdamage", "This will decrease the damage taken");

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
