package com.nosad.sample.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.SupportMapFragment;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.SpellsAdapter;
import com.nosad.sample.entity.Spell;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameMapFragment.OnPauseGameListener} interface
 * to handle interaction events.
 */
public class GameMapFragment extends Fragment {
    private SupportMapFragment mapFragment;
    private MainActivity activity;
    private OnPauseGameListener onPauseGameListener;

    private ProfilePictureView userProfilePicture;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView tvHealthPoints, tvMentalPoints;

    private ActionMode spellMode;

    private SpellsAdapter spellsAdapter;

    public GameMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
    }

    @Override
    public void onResume() {
        Log.v(Constants.TAG, this.toString() + " onResume()");
        super.onResume();
        App.getLocationManager().updateMap(mapFragment.getMap());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        userProfilePicture.setProfileId(object.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }).executeAsync();

        userProfilePicture = (ProfilePictureView) view.findViewById(R.id.mapUserAvatar);

        tvHealthPoints = (TextView) view.findViewById(R.id.tvHealthPoints);
        tvHealthPoints.setText(String.format(getResources().getString(R.string.health_points), 100));

        tvMentalPoints = (TextView) view.findViewById(R.id.tvMentalPoints);
        tvMentalPoints.setText(String.format(getResources().getString(R.string.mental_points), 50));

        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerMap);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                activity, drawerLayout, activity.getToolbarTop(),
                R.string.drawerOpened,
                R.string.drawerClosed) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                activity.getSupportActionBar().setTitle(activity.getTitle());
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activity.getSupportActionBar().setTitle(activity.getTitle());
                activity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        spellsAdapter = new SpellsAdapter(activity, R.layout.spell_item);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        drawerList = (ListView) view.findViewById(R.id.lvSpells);
        drawerList.setAdapter(spellsAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (spellMode == null) {
                    App.getSpellManager().setIsSpellMode(true);
                    App.getSpellManager().setCurrentSpell(spellsAdapter.getItem(position));
                    spellMode = activity.startSupportActionMode(new ActionBarSpell());
                    spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), spellsAdapter.getItem(position).getTitle()));
                } else {
                    if (App.getSpellManager().getCurrentSpell() == spellsAdapter.getItem(position)) {
                        App.getSpellManager().setIsSpellMode(false);
                        spellMode.finish();
                        spellMode = null;
                    } else {
                        App.getSpellManager().setCurrentSpell(spellsAdapter.getItem(position));
                        spellMode.setTitle(String.format(getResources().getString(R.string.spell_selected), spellsAdapter.getItem(position).getTitle()));
                    }
                }
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
        try {
            onPauseGameListener = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        Log.v(Constants.TAG, this.toString() + " onPause()");
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPauseGameListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPauseGameListener {
        void onPauseGame();
    }

    private class ActionBarSpell implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.spell_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            spellMode.finish();
            spellMode = null;
        }
    }
}