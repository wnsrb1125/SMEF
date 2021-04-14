package com.shelper.overlay;

import android.content.Context;
import android.util.AttributeSet;

public class FloatingView {
    Context context;
    AttributeSet attributeSet;
    int def = 0;

    public FloatingView(Context context) {
        this.context = context;
    }

    public FloatingView(Context context, AttributeSet attributeSet) {
        this.context = context;
        this.attributeSet = attributeSet;
    }

    public FloatingView(Context context, AttributeSet attributeSet, int def) {
        this.context = context;
        this.attributeSet = attributeSet;
        this.def = def;
    }


}
