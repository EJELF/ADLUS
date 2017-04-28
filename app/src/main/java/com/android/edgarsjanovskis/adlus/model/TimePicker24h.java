package com.android.edgarsjanovskis.adlus.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class TimePicker24h extends TimePicker {

    public TimePicker24h(Context context) {
        super(context);
        init();
    }

    public TimePicker24h(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePicker24h(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setIs24HourView(true);
    }

}