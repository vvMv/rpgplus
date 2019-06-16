package com.vmv.rpgplus.skill;

import org.bukkit.Material;

public enum SkillType {

    ARCHERY(Material.BOW, Material.CROSSBOW),
    ATTACK(Material.DIAMOND_SWORD, Material.STONE_SWORD, Material.GOLDEN_SWORD, Material.IRON_SWORD, Material.WOODEN_SWORD),
    FARMING(Material.DIAMOND_HOE, Material.STONE_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.WOODEN_HOE),
    FISHING(Material.FISHING_ROD),
    MINING(Material.DIAMOND_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.WOODEN_PICKAXE),
    STAMINA,
    WOODCUTTING(Material.DIAMOND_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.WOODEN_AXE);

    private Material[] materials = null;

    SkillType(Material... materials) {
        this.materials = materials;
    }

    public Material[] getMaterials() {
        return materials;
    }
}
