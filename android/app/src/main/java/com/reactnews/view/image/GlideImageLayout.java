package com.reactnews.view.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by hitomi on 2018/4/4.
 */

public class GlideImageLayout extends FrameLayout {
    private ImageView innerImg;
    private ImageView outerImg;

    public GlideImageLayout(@NonNull Context context) {
        this(context, null);
    }

    public GlideImageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GlideImageLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams imgLp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        innerImg = new ImageView(context);
        outerImg = new ImageView(context);

        innerImg.setVisibility(INVISIBLE);
        outerImg.setVisibility(INVISIBLE);

        addView(innerImg);
        addView(outerImg);
    }

    public ImageView getInnerImg() {
        return innerImg;
    }

    public ImageView getOuterImg() {
        return outerImg;
    }

    public ImageView preShowPlaceHolder(){
        innerImg.setVisibility(INVISIBLE);
        outerImg.setVisibility(VISIBLE);
        return outerImg;
    }

    public ImageView showInnerImg() {
        innerImg.setVisibility(VISIBLE);
        outerImg.setVisibility(GONE);
        return innerImg;
    }

    public ImageView showOuterImg() {
        innerImg.setVisibility(GONE);
        outerImg.setVisibility(VISIBLE);
        return outerImg;
    }
}
