package com.vmv.rpgplus.database;

import com.vmv.core.config.FileManager;

public enum PlayerSetting {

    //As SQLLite doesn't have a bool datatype 0/1 is used instead
    //Archery
    MULTI_ARROW,
    EXPLOSIVE_ARROW,
    TELEPORT_ARROW,
    SPLIT_SHOT,

    //Attack
    TRACK,
    LIFE_STEAL,

    //Farming

    //Fishing

    //Mining
    ORE_LOCATOR,
    VEIN_MINER,

    //Stamina
    DASH,
    HEALTH,
    FEATHER_FALLING,

    //Woodcutting
    TREE_FELLER,

    //Misc
    EXPERIENCE_POPUPS,
    LEVELUP_MESSAGES,
    REMINDER_MESSAGES;

    private Boolean defaultValue;

    PlayerSetting() {
        this.defaultValue = FileManager.getSettings().getBoolean(this.name());
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }
}
