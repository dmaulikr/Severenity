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
import com.severenity.databinding.ChipGridItemBinding;
import com.severenity.databinding.FragmentChipInfoBinding;
import com.severenity.entity.chip.Chip;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChipInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChipInfoFragment extends DialogFragment {
    private static final String ARG_CHIP = "chip";
    private Chip chip;

    public ChipInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param chip - {@link Chip} object to show info of.
     * @return A new instance of fragment ChipInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChipInfoFragment newInstance(Chip chip) {
        ChipInfoFragment fragment = new ChipInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHIP, chip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chip = getArguments().getParcelable(ARG_CHIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (chip == null) {
            return inflater.inflate(R.layout.fragment_chip_info, container);
        }

        // Inflate the layout for this fragment
        FragmentChipInfoBinding fragmentChipInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chip_info, container, false);
        fragmentChipInfoBinding.setChip(chip);

        getDialog().setCanceledOnTouchOutside(true);

        ImageView close = (ImageView) fragmentChipInfoBinding.getRoot().findViewById(R.id.ivClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return fragmentChipInfoBinding.getRoot();
    }
}
