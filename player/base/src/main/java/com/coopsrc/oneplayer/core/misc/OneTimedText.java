package com.coopsrc.oneplayer.core.misc;

import android.graphics.Rect;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-10 10:23
 */
public class OneTimedText implements ITimedText {

    public OneTimedText(Rect textBounds, String textChars) {
        mTextBounds = textBounds;
        mTextChars = textChars;
    }

    private Rect mTextBounds = null;
    private String mTextChars = null;

    @Override
    public Rect getBounds() {
        return mTextBounds;
    }

    @Override
    public String getText() {
        return mTextChars;
    }
}
