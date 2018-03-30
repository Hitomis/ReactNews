package com.reactnews.view.multipage;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ScaleCenterMode implements ItemViewMode {

    private float scaleRatio = 0.835f;

    public ScaleCenterMode() {
    }

    public ScaleCenterMode(float scaleRatio) {
        this.scaleRatio = scaleRatio;
    }

    @Override
    public void applyToView(View v, RecyclerView parent) {
        int parentHalfWidth = parent.getWidth() / 2;
        if (isCenterView(v, parentHalfWidth)) {
            ViewCompat.setScaleX(v, 1);
            ViewCompat.setScaleY(v, 1);
        } else {
            ViewCompat.setScaleX(v, scaleRatio);
            ViewCompat.setScaleY(v, scaleRatio);
        }
    }

    private boolean isCenterView(View v, int halfWidth) {
        final int x0 = v.getLeft();
        final int x1 = v.getWidth() + x0;
        return halfWidth >= x0 && halfWidth <= x1;
    }
}
