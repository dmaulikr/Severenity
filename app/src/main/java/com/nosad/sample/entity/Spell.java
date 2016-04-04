package com.nosad.sample.entity;

import android.graphics.drawable.Drawable;

import com.nosad.sample.R;

/**
 * Created by Novosad on 4/4/16.
 */
public class Spell {
    public enum SpellType {
        Ward,
        PowerWave
    }

    private SpellType spellType;
    private int spellIconResource;

    public Spell(SpellType spellType) {
        this.spellType = spellType;

        switch (spellType) {
            case Ward:
                this.spellIconResource = R.drawable.spell_ward;
                break;
            case PowerWave:
                this.spellIconResource = R.drawable.spell_wave;
                break;
        }
    }

    public String getTitle() {
        switch (spellType) {
            case Ward:
                return "Ward";
            case PowerWave:
                return "Power Wave";
            default:
                return "Unkown";
        }
    }

    public int getSpellIconResource() {
        return spellIconResource;
    }

    public void setSpellIconResource(int spellIconResource) {
        this.spellIconResource = spellIconResource;
    }

    public boolean is(SpellType spellType) {
        return this.spellType == spellType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Spell)) {
            return false;
        }

        Spell s = (Spell) o;
        return this.spellType == s.spellType;
    }
}
