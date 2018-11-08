package com.naijaplanet.magosla.android.thebaker.ui.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RecipeListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeListViewRemoteService(this.getApplicationContext(), intent);
    }
}
