package com.nosad.sample.engine.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nosad.sample.databinding.QuestItemBinding;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class is responsible for quests display in a list.
 *
 * Created by Novosad on 4/4/16.
 */
public class QuestsAdapter extends RecyclerView.Adapter<QuestsAdapter.QuestViewHolder> {
    // Couple test quests
    private ArrayList<Quest> quests = new ArrayList<>();
    private MainActivity context;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public QuestsAdapter(MainActivity context, List<Quest> objects) {
        this.context = context;
        this.quests.addAll(objects);
    }

    public class QuestViewHolder extends RecyclerView.ViewHolder {
        private QuestItemBinding questItemBinding;

        public QuestViewHolder(View v) {
            super(v);
            questItemBinding = DataBindingUtil.bind(v);
            v.setOnCreateContextMenuListener(context);
        }
    }

    public Quest getItem(int position) {
        return quests.get(position);
    }

    public int getPosition(Quest item) {
        int index = -1;

        for (int i = 0; i < quests.size(); i++) {
            if (quests.get(i).equals(item)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void addAll(ArrayList<Quest> quests) {
        this.quests.addAll(quests);
        notifyDataSetChanged();
    }

    public void add(Quest object) {
        if (object != null) {
            quests.add(object);
        }
        notifyDataSetChanged();
    }

    public void remove(Quest object) {
        for (Quest quest : quests) {
            if (quest.getId() == object.getId()) {
                quests.remove(quest);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void refreshWith(Collection<? extends Quest> collection) {
        quests.clear();
        addAll(collection);
    }

    public void addAll(Collection<? extends Quest> collection) {
        if (collection != null) {
            quests.addAll(collection);
        }
        notifyDataSetChanged();
    }

    @Override
    public QuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        QuestItemBinding questItemBinding = QuestItemBinding.inflate(inflater, parent, false);
        return new QuestViewHolder(questItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(final QuestViewHolder holder, int position) {
        Quest quest = quests.get(position);
        holder.questItemBinding.setQuest(quest);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(QuestViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
