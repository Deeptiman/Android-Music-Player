package com.example.musicplayer.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends TextInputEditText {


    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf,style);

        Typeface face = Typeface.createFromAsset(getContext().getAssets(),"fonts/BebasNeue.otf");
        setTypeface(face);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);

        setTextColor(Color.WHITE);
    }
}
