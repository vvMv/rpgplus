package com.vmv.core.config;

import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class FileManager {

    private static PluginFile lang;
    private static PluginFile config;
    private static HashMap<SkillType, PluginFile> sFiles;
    private String language = "eng"; //TODO Create a config variable for valid language files with information checking

    public FileManager(JavaPlugin plugin) {
        lang = new PluginFile(plugin, plugin.getDataFolder() + File.separator, "lang-" + language + ".yml");
        sFiles = new HashMap<SkillType, PluginFile>();
        config = new PluginFile(plugin, plugin.getDataFolder() + File.separator, "config.yml");

        for (SkillType s : SkillType.values()) {
            sFiles.put(s, new PluginFile(plugin, plugin.getDataFolder() + File.separator + "skill" + File.separator, s.name().toLowerCase() + ".yml"));
        }

    }

    public static PluginFile getLang() {
        return lang;
    }

    public static PluginFile getConfig() {
        return config;
    }

    public static PluginFile getSkillFile(SkillType s) {
        return sFiles.get(s);
    }
}
