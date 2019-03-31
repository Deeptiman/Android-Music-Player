package com.example.musicplayer.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class CustomTextView extends TextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf,style);

        Typeface face = Typeface.createFromAsset(getContext().getAssets(),"fonts/BebasNeue.otf");
        setTypeface(face);
    }

}

