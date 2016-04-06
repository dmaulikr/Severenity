package com.nosad.sample.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.nosad.sample.R;

import java.io.InputStream;

/**
 * Created by Novosad on 4/5/16.
 */
public class GifView extends View {

    private InputStream inputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration, movieStart;

    public GifView(Context context) {
        super(context);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        inputStream = context.getResources().openRawResource(+R.drawable.spell_wave_animation);
        gifMovie = Movie.decodeStream(inputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(0.5f, 0.5f);
        long now = SystemClock.currentThreadTimeMillis();

        if (movieStart == 0) {
            movieStart = now;
        }

        if (gifMovie != null) {
            int duration = gifMovie.duration();

            int realTime = (int) ((now - movieStart) % duration);
            gifMovie.setTime(realTime);
            gifMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }
}
