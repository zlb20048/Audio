package com.kallaite.media.fragment.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by liuzixiang on 15-12-17.
 */
public class MarqueeTextView extends TextView
{
    public MarqueeTextView(Context context)
    {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }
}
