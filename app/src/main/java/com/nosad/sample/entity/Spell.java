package com.nosad.sample.entity;

import com.nosad.sample.R;

/**
 * Created by Novosad on 4/4/16.
 */
public class Spell {
    public enum SpellType {
        Capture,
        Dispel,
        Shield,
        Attack
    }

    private SpellType spellType;
    private int spellIconResource;

    public Spell(SpellType spellType) {
        this.spellType = spellType;

        switch (spellType) {
            case Capture:
                this.spellIconResource = R.drawable.menu_marker;
                break;
            case Dispel:
                this.spellIconResource = R.drawable.spell_wave;
                break;
            case Shield:
                this.spellIconResource = R.drawable.spell_ward;
                break;
            case Attack:
                this.spellIconResource = R.drawable.menu_arrow_right;
                break;
        }
    }

    public String getTitle() {
        switch (spellType) {
            case Capture:
                return "Capture";
            case Dispel:
                return "Dispell";
            case Shield:
                return "Shield";
            case Attack:
                return "Attack";
            default:
                return "Unknown";
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
