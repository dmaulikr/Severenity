package com.severenity.view.custom;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.databinding.SkillGridItemBinding;
import com.severenity.entity.skill.Skill;
import com.severenity.view.dialogs.SkillInfoFragment;
import com.severenity.view.activities.MainActivity;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/17/16.
 */
public class SkillRecycleViewAdapter extends RecyclerView.Adapter<SkillRecycleViewAdapter.SkillViewHolder> {
    private ArrayList<Skill> skillList;
    private MainActivity context;

    public SkillRecycleViewAdapter(MainActivity context, ArrayList<Skill> itemList) {
        this.skillList = itemList;
        this.context = context;
    }

    @Override
    public SkillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final SkillGridItemBinding skillGridItemBinding = SkillGridItemBinding.inflate(inflater, parent, false);
        skillGridItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkillInfoFragment skillInfoFragment = SkillInfoFragment.newInstance(skillGridItemBinding.getSkill());
                skillInfoFragment.show(context.getSupportFragmentManager(), SkillInfoFragment.class.getSimpleName());
            }
        });

        return new SkillViewHolder(skillGridItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(SkillViewHolder holder, int position) {
        Skill skill = skillList.get(position);
        holder.skillGridItemBinding.setSkill(skill);
    }

    @Override
    public int getItemCount() {
        return this.skillList.size();
    }

    class SkillViewHolder extends RecyclerView.ViewHolder {
        private SkillGridItemBinding skillGridItemBinding;

        SkillViewHolder(View view) {
            super(view);
            skillGridItemBinding = DataBindingUtil.bind(view);
        }
    }
}
