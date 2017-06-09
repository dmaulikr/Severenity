package com.severenity.view.Dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.InfoAdapter;
import com.severenity.engine.adapters.UserInfoAdapter;
import com.severenity.entity.GamePlace;

import java.util.ArrayList;

/**
 * Created by Novosad on 6/9/17.
 */

public class PlacesOwnedDialog extends DialogFragment {

    public static PlacesOwnedDialog newInstance() {
        PlacesOwnedDialog placesOwnedDialog = new PlacesOwnedDialog();
        return placesOwnedDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_places_owned_info, null);
        setCancelable(true);

        ListView mPlacesList = (ListView)view.findViewById(R.id.listOwnPlaces);
        UserInfoAdapter infoAdapter = new UserInfoAdapter(getContext());

        ArrayList<GamePlace> places = App.getPlacesManager().findPlacesByOwner(App.getUserManager().getCurrentUser().getId());
        for (GamePlace gp: places) {
            InfoAdapter.InfoData data = new InfoAdapter.InfoData();
            data.dataID = gp.getPlaceID();
            data.dataString = gp.getPlaceName();
            infoAdapter.addItem(data);
        }
        mPlacesList.setAdapter(infoAdapter);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
