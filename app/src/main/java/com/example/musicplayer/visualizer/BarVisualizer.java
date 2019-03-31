/*
 * Copyright (C) 2017 Gautam Chibde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.musicplayer.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.apppreference.AppSharedPrefrence;

import java.util.Arrays;


/**
 * Custom view that creates a Bar visualizer effect for
 * the android {@link android.media.MediaPlayer}
 * <p>
 * Created by gautam chibde on 28/10/17.
 */

public class BarVisualizer extends View {

    public static float density = 10;
    private int gap;

    private Paint paint;

    Context mContext;
    AppSharedPrefrence mAppSharedPrefrence;
    public static String TAG = "MusicBarVisualizer";

    public BarVisualizer(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    protected void init() {

        this.mAppSharedPrefrence = AppSharedPrefrence.getsharedprefInstance(mContext);
        this.paint = new Paint();
        this.density = 50;
        this.gap = 4;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    /**
     * Sets the density to the Bar visualizer i.e the number of bars
     * to be displayed. Density can vary from 10 to 256.
     * by default the value is set to 50.
     *
     * @param density density of the bar visualizer
     */
    public void setDensity(float density) {
        this.density = density;
        if (density > 256) {
            this.density = 256;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        byte[] bytes = mAppSharedPrefrence.getVisualizerByteArray();

        if (bytes != null) {

            Log.d(MainActivity.VISUALIZE_TAG,"onDraw : "+ Arrays.toString(bytes));

            float barWidth = getWidth() / density;
            float div = bytes.length / density;

            paint.setStrokeWidth(10);

            for (int i = 0; i < density; i++) {
                int bytePosition = (int) Math.ceil(i * div);
                int top = getHeight() +
                        ((byte) (Math.abs(bytes[bytePosition]) + 128)) * getHeight() / 128;
                float barX = (i * barWidth) + (barWidth / 2);
                canvas.drawLine(barX, getHeight(), barX, top, paint);
            }
            super.onDraw(canvas);
        }
    }
}
