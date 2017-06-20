package com.severenity.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.severenity.R;

public class CustomAlertDialog extends DialogFragment {

    // Object that is going to show this dialog can implement
    // this interface so it will have callback methods for
    // monitoring buttons click
    public interface ButtonClickListener {

        // notifies that Ok/Yes button clicked
        void OnOkButtonClick();

        // notifies that cancel button clicked
        void OnCancelButtonClick();
    }
    private ButtonClickListener mListener;

    public static CustomAlertDialog newInstance(int title, ButtonClickListener listener) {
        CustomAlertDialog frag = new CustomAlertDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        frag.setOnClickListener(listener);
        return frag;
    }

    private void setOnClickListener(ButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(getString(R.string.yes_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.OnOkButtonClick();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.OnCancelButtonClick();
                            }
                        }
                )
                .create();
    }
}