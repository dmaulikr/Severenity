package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.R;
import com.nosad.sample.entity.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Novosad on 4/4/16.
 */
public class ChipAdapter extends ArrayAdapter<Chip> {
    private ArrayList<Chip> chips = new ArrayList<>(Arrays.asList(
            new Chip(Chip.ChipType.Capture),
            new Chip(Chip.ChipType.Dispel),
            new Chip(Chip.ChipType.Shield),
            new Chip(Chip.ChipType.Attack)
    ));
    private Context context;
    private int resource;

    public ChipAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public ChipAdapter(Context context, int resource, List<Chip> objects) {
        this(context, resource);
        this.chips = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return chips.size();
    }

    @Override
    public Chip getItem(int position) {
        return chips.get(position);
    }

    @Override
    public int getPosition(Chip item) {
        int index = -1;

        for (int i = 0; i < chips.size(); i++) {
            if (chips.get(i).equals(item)) {
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chip chip = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvSpellTitle);
        tvTitle.setText(chip.getTitle());

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivSpellIcon);
        ivIcon.setImageDrawable(
            context.getResources().getDrawable(chip.getChipIconResource(), context.getTheme())
        );

        return convertView;
    }
}