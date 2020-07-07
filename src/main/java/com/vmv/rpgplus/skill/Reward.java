package com.vmv.rpgplus.skill;

import com.cryptomorin.xseries.XSound;
import org.bukkit.event.Listener;

import java.util.List;

public class Reward implements Listener {

    private String id;
    private Boolean allskills;
    private List<Integer> levels;
    private List<SkillType> skillTypes;
    private List<String> commands;
    private List<String> messages;
    private List<XSound> sounds;

    public Reward(String id, Boolean allskills, List<Integer> levels, List<SkillType> skillTypes, List<String> commands, List<String> messages, List<XSound> sounds) {
        this.id = id;
        this.allskills = allskills;
        this.levels = levels;
        this.skillTypes = skillTypes;
        this.commands = commands;
        this.messages = messages;
        this.sounds = sounds;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public List<SkillType> getSkillTypes() {
        return skillTypes;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<XSound> getSounds() {
        return sounds;
    }

    public String getId() {
        return id;
    }

    public Boolean isAllskills() {
        return allskills;
    }
}
