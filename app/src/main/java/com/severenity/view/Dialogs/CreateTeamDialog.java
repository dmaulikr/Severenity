package com.severenity.view.Dialogs;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.severenity.R;

/**
 * Created by Andriy on 8/25/2016.
 */
public class CreateTeamDialog extends DialogFragment implements View.OnClickListener {

    public interface OnTeamCreatedListener {
        void OnTeamCreated();
    }

    // listener that might handle event once team is created
    private OnTeamCreatedListener mListener;

    public static CreateTeamDialog newInstance() {
        CreateTeamDialog frag = new CreateTeamDialog();
        return frag;
    }

    public void setListener(OnTeamCreatedListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_team, null);

        ((Button)view.findViewById(R.id.btnCreateTeam)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.btnCancel)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                super.dismiss();
        }
    }
}
