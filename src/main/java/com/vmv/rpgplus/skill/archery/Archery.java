package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;

import static com.vmv.rpgplus.skill.SkillType.ARCHERY;

public class Archery extends Skill {

    public Archery(SkillType skillType) {
        super(skillType);
        //registerAbility(new MultiArrow("multi_arrow", ARCHERY), new ExplosiveArrow("explosive_arrow", ARCHERY), new TeleportArrow("teleport_arrow", ARCHERY), new SplitShot("split_shot", ARCHERY));
    }

}
