package com.gmonetix.slambook.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * @author Gmonetix
 */

public class RobotoTextView extends android.support.v7.widget.AppCompatTextView {


    public RobotoTextView(Context context) {
        super(context);
    }

    public RobotoTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyRobotoFont(context);
    }

    public RobotoTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        applyRobotoFont(context);
    }

    private void applyRobotoFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/RobotoLight.ttf", context);
        setTypeface(customFont);
    }

}
