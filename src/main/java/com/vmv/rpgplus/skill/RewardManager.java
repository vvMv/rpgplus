package com.vmv.rpgplus.skill;

import com.cryptomorin.xseries.XSound;
import com.vmv.core.config.FileManager;
import com.vmv.core.config.PluginFile;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class RewardManager implements Listener {

    private static RewardManager instance;
    private List<Reward> rewards;

    public RewardManager() {
        instance = this;
        RPGPlus.getInstance().registerEvents(this);
        reload();
    }

    @EventHandler
    public void onLevelUp(LevelModifyEvent e) {
        for (int i = e.getFromLevel() + 1; i < e.getToLevel() + 1; i++) {
            for (Reward reward : rewards) {
                if (!reward.isAllskills()) {
                    if (!reward.getSkillTypes().contains(e.getSkill())) continue;
                }
                InformationHandler.printMessage(InformationType.DEBUG, reward.getLevels().size() + " " + e.getToLevel());
                if (!reward.getLevels().contains(e.getToLevel())) continue;
                Player p = e.getPlayer();
                int finalI = i;
                reward.getMessages().forEach(s -> ChatUtil.sendChatMessage(p, s.replace("%p", p.getName()).replace("%l", String.valueOf(finalI))));
                reward.getCommands().forEach(s -> RPGPlus.getInstance().getServer().dispatchCommand(RPGPlus.getInstance().getServer().getConsoleSender(), s.replace("%p", p.getName()).replace("%l", String.valueOf(finalI))));
                reward.getSounds().forEach(xSound -> p.getWorld().playSound(p.getLocation(), xSound.parseSound(), 1.0f, 1.0f));
            }
        }
    }

    public void reload() {
        this.rewards = new ArrayList<>();
        PluginFile rewards = FileManager.getRewards();
        for (String key : rewards.getKeys(false)) {

            Boolean allskills = rewards.getBoolean(key + ".all_skills");
            List<Integer> levels = new ArrayList<>();
            List<SkillType> skillTypes = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            List<String> messages = new ArrayList<>();
            List<XSound> sounds = new ArrayList<>();

            String levelString = rewards.getString(key + ".level");
            if (levelString.split("-").length == 1) {
                levels.add(Integer.valueOf(levelString));
            } else if (levelString.split("-").length == 2) {
                for (int i = Integer.valueOf(levelString.split("-")[0]); i <= Integer.valueOf(levelString.split("-")[1]); i++) { //Lower bounds to upper bounds
                    levels.add(i);
                }
            }

            for (String st : rewards.getStringList(key + ".skills")) {
                if (SkillType.valueOf(st.toUpperCase()) != null) skillTypes.add(SkillType.valueOf(st.toUpperCase()));
            }

            for (String value : rewards.getStringList(key + ".rewards")) {
                if (value.split(":").length != 2) continue;
                String left = value.split(":")[0];
                String right = value.split(":")[1];
                if (left.equalsIgnoreCase("command")) commands.add(right);
                if (left.equalsIgnoreCase("message")) messages.add(right);
                try {
                    if (left.equalsIgnoreCase("sound")) sounds.add(XSound.valueOf(right));
                } catch (Exception ex) {
                    InformationHandler.printMessage(InformationType.ERROR, "Sound value " + right + " is invalid for " + key + ".rewards");
                }
            }

            Reward r = new Reward(key, allskills, levels, skillTypes, commands, messages, sounds);
            this.rewards.add(r);

        }

        InformationHandler.printMessage(InformationType.INFO, "Registered " + this.rewards.size() + " reward categories");

    }

    public static RewardManager getInstance() {
        return instance;
    }

}
