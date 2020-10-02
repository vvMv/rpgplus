package com.vmv.rpgplus.skill;

import com.vmv.core.config.FileManager;

public enum AbilityAttribute {

    /**
     * @identifier Must be the value of a config value within the ability
     */
    DECREASE_COOLDOWN("cooldown"),
    INCREASE_DURATION("duration"),
    INCREASE_SPEED("speed"),
    INCREASE_HEARTS("hearts"),
    INCREASE_RANGE("range"),
    INCREASE_ARROWS("arrows"),
    INCREASE_EXPLOSION("explosion"),
    DECREASE_SELFDAMAGE("selfdamage"),
    INCREASE_LIFE_STEAL("life_steal"),
    INCREASE_CHANCE("chance"),
    INCREASE_REDUCTION("reduction"),
    INCREASE_CRITICAL("critical_damage");

    String identifier;

    AbilityAttribute(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        try {
            return FileManager.getLang().getString("attribute." + this.name().toLowerCase() + "_description");
        } catch (Exception ignore) {
            return "invalid description";
        }
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

    public String getFormattedName() {
        try {
            return FileManager.getLang().getString("attribute." + this.name().toLowerCase());
        } catch (Exception ignore) {
            return this.name().toLowerCase().replace("_", " ");
        }
    }

}
