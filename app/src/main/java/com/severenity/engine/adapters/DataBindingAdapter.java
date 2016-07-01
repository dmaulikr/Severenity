package com.severenity.engine.adapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by Novosad on 5/17/16.
 */
public class DataBindingAdapter {
    @BindingAdapter("chipIconResource")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
