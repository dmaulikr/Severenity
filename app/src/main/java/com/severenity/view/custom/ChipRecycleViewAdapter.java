package com.severenity.view.custom;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.databinding.ChipGridItemBinding;
import com.severenity.entity.chip.Chip;
import com.severenity.view.Dialogs.ChipInfoFragment;
import com.severenity.view.activities.MainActivity;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/17/16.
 */
public class ChipRecycleViewAdapter extends RecyclerView.Adapter<ChipRecycleViewAdapter.ChipViewHolder> {
    private ArrayList<Chip> chipList;
    private MainActivity context;

    public ChipRecycleViewAdapter(MainActivity context, ArrayList<Chip> itemList) {
        this.chipList = itemList;
        this.context = context;
    }

    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ChipGridItemBinding chipGridItemBinding = ChipGridItemBinding.inflate(inflater, parent, false);
        chipGridItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChipInfoFragment chipInfoFragment = ChipInfoFragment.newInstance(chipGridItemBinding.getChip());
                chipInfoFragment.show(context.getSupportFragmentManager(), ChipInfoFragment.class.getSimpleName());
            }
        });

        return new ChipViewHolder(chipGridItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(ChipViewHolder holder, int position) {
        Chip chip = chipList.get(position);
        holder.chipGridItemBinding.setChip(chip);
    }

    @Override
    public int getItemCount() {
        return this.chipList.size();
    }

    public class ChipViewHolder extends RecyclerView.ViewHolder {
        private ChipGridItemBinding chipGridItemBinding;

        public ChipViewHolder(View view) {
            super(view);
            chipGridItemBinding = DataBindingUtil.bind(view);
        }
    }
}
