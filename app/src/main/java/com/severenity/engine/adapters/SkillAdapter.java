package com.severenity.engine.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.skill.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Novosad on 4/4/16.
 */
public class SkillAdapter extends ArrayAdapter<Skill> {
    private ArrayList<Skill> skills = new ArrayList<>(Arrays.asList(
            new Skill(Skill.SkillType.CapturePlace, "Captures place to collect data that improves your implant.", 0, Skill.Rarity.Common)
    ));

    private Context context;
    private int resource;

    public SkillAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public SkillAdapter(Context context, int resource, List<Skill> objects) {
        this(context, resource);
        this.skills = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return skills.size();
    }

    @Override
    public Skill getItem(int position) {
        return skills.get(position);
    }

    @Override
    public int getPosition(Skill item) {
        int index = -1;

        for (int i = 0; i < skills.size(); i++) {
            if (skills.get(i).equals(item)) {
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Skill skill = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvSpellTitle);

        if (skill != null) {
            tvTitle.setText(skill.getTitle());
            ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivSpellIcon);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivIcon.setImageDrawable(
                        context.getResources().getDrawable(skill.getSkillIconResource(), context.getTheme())
                );
            } else {
                ivIcon.setImageDrawable(context.getResources().getDrawable(skill.getSkillIconResource()));
            }
        }

        return convertView;
    }
}
