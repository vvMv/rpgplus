package com.vmv.rpgplus.database;

public enum PlayerSetting {

    //As SQLLite doesn't have a bool datatype 0/1 is used instead
    //Archery
    MULTI_ARROW("1"),
    EXPLOSIVE_ARROW("1"),
    TELEPORT_ARROW("1"),
    SPLIT_SHOT("1"),

    //Attack
    TRACK("1"),

    //Farming

    //Fishing

    //Mining
    ORE_LOCATOR("1"),
    VEIN_MINER("1"),

    //Stamina
    DASH("1"),
    HEALTH("1"),

    //Woodcutting
    TREE_FELLER("1"),

    //Misc
    EXPERIENCE_POPUPS("1"),
    LEVELUP_MESSAGES("1");

    private String defaultValue;

    PlayerSetting(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
