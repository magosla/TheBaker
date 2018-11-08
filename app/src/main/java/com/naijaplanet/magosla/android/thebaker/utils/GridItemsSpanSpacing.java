package com.naijaplanet.magosla.android.thebaker.utils;

import android.util.DisplayMetrics;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class GridItemsSpanSpacing {
    private final int spacing;
    private final boolean includeEdge;
    private int span;

    public GridItemsSpanSpacing(RecyclerView recyclerView, int itemWidthID, int itemHeightId, int orientation, boolean includeEdge, boolean dimensionAsResourceId) {
        float boxSize, containerSize;
        this.includeEdge = includeEdge;
        DisplayMetrics displayMetrics = recyclerView.getResources().getDisplayMetrics();
        if (orientation == RecyclerView.VERTICAL && itemWidthID > 0) {
            int itemPixelWidth = dimensionAsResourceId ? recyclerView.getResources()
                    .getDimensionPixelSize(itemWidthID) : itemWidthID;
            span = displayMetrics.widthPixels / itemPixelWidth;
            boxSize = itemPixelWidth;
            containerSize = displayMetrics.widthPixels;
        } else if (orientation == RecyclerView.HORIZONTAL && itemHeightId > 0) {
            int itemPixelHeight = dimensionAsResourceId ? recyclerView.getResources()
                    .getDimensionPixelSize(itemHeightId) : itemHeightId;
            span = displayMetrics.heightPixels / itemPixelHeight;
            boxSize = itemPixelHeight;
            containerSize = displayMetrics.heightPixels;
        } else {
            // just set some default values to prevent errors
            span = 1;
            boxSize = 1;
            containerSize = 2;
            Log.d(this.getClass().getSimpleName(), "Orientation, (itemResourceWidthID or itemResourceHeightID depending on orientation) is required");
        }
        float remainingSpace = containerSize - (boxSize * span);
        if (remainingSpace < 0 && span > 1) {
            span--;
            remainingSpace = containerSize - (boxSize * span);
        }
        spacing = (int) ((!includeEdge && span > 1) ? remainingSpace / (span - 1) : remainingSpace / (span + 1));
    }

    public boolean isIncludeEdge() {
        return includeEdge;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getSpan() {
        return span;
    }
}
