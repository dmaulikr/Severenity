package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.R;
import com.nosad.sample.entity.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Novosad on 4/4/16.
 */
public class SpellsAdapter extends ArrayAdapter<Spell> {
    private ArrayList<Spell> spells = new ArrayList<>(Arrays.asList(
            new Spell(Spell.SpellType.Capture),
            new Spell(Spell.SpellType.Dispel),
            new Spell(Spell.SpellType.Shield),
            new Spell(Spell.SpellType.Attack)
    ));
    private Context context;
    private int resource;

    public SpellsAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public SpellsAdapter(Context context, int resource, List<Spell> objects) {
        this(context, resource);
        this.spells = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return spells.size();
    }

    @Override
    public Spell getItem(int position) {
        return spells.get(position);
    }

    @Override
    public int getPosition(Spell item) {
        int index = -1;

        for (int i = 0; i < spells.size(); i++) {
            if (spells.get(i).equals(item)) {
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Spell spell = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvSpellTitle);
        tvTitle.setText(spell.getTitle());

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivSpellIcon);
        ivIcon.setImageDrawable(
            context.getResources().getDrawable(spell.getSpellIconResource(), context.getTheme())
        );

        return convertView;
    }
}
