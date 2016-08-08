package com.severenity.view.fragments;

/**
 * Created by Andriy on 8/8/2016.
 */
public interface NotifiableFragment {
    /*
    onFragmentShow notifies flagmen that it was hidden or shown.
    @param show - true if shown, false otherwise.
     */
    public void onFragmentShow(boolean show);
}
