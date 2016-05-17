package com.nosad.sample.view.custom;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nosad.sample.R;
import com.nosad.sample.databinding.ChipGridItemBinding;
import com.nosad.sample.entity.Chip;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/17/16.
 */
public class ChipRecycleViewAdapter extends RecyclerView.Adapter<ChipRecycleViewAdapter.ChipViewHolder> {
    private ArrayList<Chip> chipList;
    private Context context;

    public ChipRecycleViewAdapter(Context context, ArrayList<Chip> itemList) {
        this.chipList = itemList;
        this.context = context;
    }

    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ChipGridItemBinding chipGridItemBinding = ChipGridItemBinding.inflate(inflater, parent, false);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Selected chip: " + chipList.get(getLayoutPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
