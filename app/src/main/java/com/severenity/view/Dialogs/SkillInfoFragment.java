package com.severenity.view.Dialogs;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.severenity.R;
import com.severenity.databinding.FragmentSkillInfoBinding;
import com.severenity.entity.skill.Skill;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SkillInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SkillInfoFragment extends DialogFragment {
    private static final String ARG_CHIP = "skill";
    private Skill skill;

    public SkillInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param skill - {@link Skill} object to show info of.
     * @return A new instance of fragment SkillInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SkillInfoFragment newInstance(Skill skill) {
        SkillInfoFragment fragment = new SkillInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHIP, skill);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            skill = getArguments().getParcelable(ARG_CHIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (skill == null) {
            return inflater.inflate(R.layout.fragment_skill_info, container);
        }

        // Inflate the layout for this fragment
        FragmentSkillInfoBinding fragmentSkillInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_skill_info, container, false);
        fragmentSkillInfoBinding.setSkill(skill);

        getDialog().setCanceledOnTouchOutside(true);

        ImageView close = (ImageView) fragmentSkillInfoBinding.getRoot().findViewById(R.id.ivClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return fragmentSkillInfoBinding.getRoot();
    }
}
