package com.severenity.view.dialogs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.quest.Quest;

/**
 * Created by Novosad on 8/25/2016.
 */
public class QuestReceivedDialogFragment extends DialogFragment {
    private static Quest quest;

    public static QuestReceivedDialogFragment newInstance(Quest q) {
        QuestReceivedDialogFragment fragment = new QuestReceivedDialogFragment();
        quest = q;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_new_quest_received_info, null);
        setCancelable(false);

        TextView questTitle = (TextView) view.findViewById(R.id.tvDialogQuestTitle);
        questTitle.setText(quest.getTitle());

        TextView questDescription = (TextView) view.findViewById(R.id.tvDialogQuestDescription);
        if (quest.getId().equals("0")) {
            questDescription.setText(getResources().getString(R.string.initial_quest));
        } else {
            questDescription.setText(quest.getDescription());
        }

        TextView questRewardExp = (TextView) view.findViewById(R.id.tvDialogQuestExpReward);
        questRewardExp.setText(String.valueOf(quest.getExperience()));

        TextView questRewardCredits = (TextView) view.findViewById(R.id.tvDialogQuestCreditsReward);
        questRewardCredits.setText(String.valueOf(quest.getCredits()));

        Button questAccept = (Button) view.findViewById(R.id.btnDialogQuestAccept);
        questAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        App.getQuestManager().onQuestAccepted(quest);
                        dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getDialog() == null || getDialog().getWindow() == null) {
            return;
        }

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
