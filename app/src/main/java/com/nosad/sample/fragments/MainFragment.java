package com.nosad.sample.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.nosad.sample.R;
import com.nosad.sample.activities.LoginActivity;
import com.nosad.sample.activities.MainActivity;
import com.nosad.sample.adapters.SamplesAdapter;
import com.nosad.sample.view.NonSwipeableViewPager;
import com.nosad.sample.view.ZoomOutPageTransformer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.nosad.sample.fragments.MainFragment.OnResumeGameListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {
    // TODO: Rename and change types of parameters
    public Profile profile;
    public AccessToken accessToken;

    private AppCompatActivity activity;

    private TextView tvFacebookUsername;
    private Button btnPrevious, btnNext;

    private NonSwipeableViewPager vpSamples;
    private SamplesAdapter samplesAdapter;

    private OnResumeGameListener onResumeGameListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button btnGo = (Button) view.findViewById(R.id.btnGo);
        tvFacebookUsername = (TextView) view.findViewById(R.id.tvFacebookUsername);

        if (((MainActivity) getActivity()).profile != null) {
            tvFacebookUsername.setText("Welcome " + ((MainActivity) getActivity()).profile.getName());
        }

        samplesAdapter = new SamplesAdapter(activity);
        vpSamples = (NonSwipeableViewPager) view.findViewById(R.id.vpCharacters);
        vpSamples.setPageTransformer(true, new ZoomOutPageTransformer());
        vpSamples.setAdapter(samplesAdapter);

        vpSamples.setCurrentItem(0);

        btnPrevious = (Button) view.findViewById(R.id.btnPrevious);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        checkNextPreviousEnabled();

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpSamples.setCurrentItem(vpSamples.getCurrentItem() - 1);
                checkNextPreviousEnabled();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpSamples.setCurrentItem(vpSamples.getCurrentItem() + 1);
                checkNextPreviousEnabled();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
        return view;
    }

    private void checkNextPreviousEnabled() {
        btnPrevious.setEnabled(!(vpSamples.getCurrentItem() == 0));
        btnNext.setEnabled(!(vpSamples.getCurrentItem() == samplesAdapter.getCount() - 1));
    }

    private void tryLogin() {
        onResumeGameListener.onResumeGame();

        // TODO: Uncomment server authentication when server done.
//        try {
//            JSONObject jsonObject = new JSONObject();
//            String facebookId = ((LoginActivity) getActivity()).accessToken.getUserId();
//            jsonObject.put("facebookId", facebookId);
//
//            RestManager.getInstance(getActivity()).createRequest(
//                    Constants.REST_API_AUTH,
//                    Request.Method.POST,
//                    jsonObject, new RequestCallback() {
//                        @Override
//                        public void onResponseCallback(JSONObject response) {
//                            Log.d(Constants.TAG, response.toString());
//                            try {
//                                if (response.getString("status").equalsIgnoreCase("success")) {
//                                    Toast.makeText(getActivity(), "User ID: " + response.getString("facebookId"), Toast.LENGTH_SHORT).show();
//                                    onResumeGameListener.onResumeGame();
//                                } else {
//                                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onErrorCallback(NetworkResponse response) {
//                            try {
//                                JSONObject json = new JSONObject(new String(response.data));
//                                Toast.makeText(getActivity(), json.toString(), Toast.LENGTH_SHORT)
//                                        .show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//            );
//            onResumeGameListener.onResumeGame();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) getActivity();
        try {
            onResumeGameListener = (OnResumeGameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onResumeGameListener = null;
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
    public interface OnResumeGameListener {
        void onResumeGame();
    }

}
