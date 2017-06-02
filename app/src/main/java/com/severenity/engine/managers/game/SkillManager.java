package com.severenity.engine.managers.game;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.severenity.entity.skill.Skill;

import java.util.ArrayList;

/**
 * Created by Novosad on 4/4/16.
 */
public class SkillManager {
    private Context context;
    private Skill currentSkill = null;
    private boolean isSkillMode = false;

    private Marker selectedWard = null;

    private ArrayList<Marker> placedWards = new ArrayList<>();

    public SkillManager(Context context) {
        this.context = context;
    }

    public Skill getCurrentSkill() {
        return currentSkill;
    }

    public void setCurrentSkill(Skill currentSkill) {
        if (this.currentSkill != null && this.currentSkill.equals(currentSkill)) {
            isSkillMode = false;
            this.setCurrentSkill(null);
            return;
        }

        isSkillMode = true;
        this.currentSkill = currentSkill;
    }

    public boolean isSkillMode() {
        return isSkillMode;
    }

    public void cancelSkillMode() {
        selectedWard = null;
        isSkillMode = false;
    }
}
