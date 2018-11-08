package com.naijaplanet.magosla.android.thebaker.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.data.provider.IngredientsColumn;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeProvider;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipesColumn;

import net.simonvt.schematic.Cursors;

@SuppressWarnings("unused")
class RecipeListViewRemoteService implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = RecipeListWidgetService.class.getSimpleName();
    private final Context mContext;
    private Cursor mCursor;

    public RecipeListViewRemoteService(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        int recipeId = Config.getUserRecipe(mContext);
        if (recipeId > 0) {
            String[] selectionArgs = {String.valueOf(recipeId)};

            mCursor = mContext.getContentResolver().query(
                    RecipeProvider.Ingredients.CONTENT_URI, null, IngredientsColumn.RECIPE_ID + "=?", selectionArgs, IngredientsColumn.ID);
        } else
            mCursor = null;
    }

    @Override
    public void onDestroy() {
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    private int getItemLayout(int i) {
        return ((i + 1) % 2) == 0 ? R.layout.recipe_item_widget_even : R.layout.recipe_item_widget_odd;
    }

    @Override
    public RemoteViews getViewAt(int i) {

        Log.i(TAG, "Widget getViewAt called at index: " + i);
        if (mCursor == null || mCursor.getCount() == 0 || mCursor.getCount() == i) return null;
        mCursor.moveToPosition(i);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), getItemLayout(i));

        long recipeId = Cursors.getLong(mCursor, IngredientsColumn.RECIPE_ID);
        String ingredient = Cursors.getString(mCursor, IngredientsColumn.INGREDIENT);
        String measure = Cursors.getString(mCursor, IngredientsColumn.MEASURE);
        String quantity = String.valueOf(Cursors.getString(mCursor, IngredientsColumn.QUANTITY));
        String iconLetter = String.valueOf(ingredient.charAt(0));

        views.setTextViewText(R.id.tv_name, ingredient);
        views.setTextViewText(R.id.tv_icon, iconLetter);
        views.setTextViewText(R.id.tv_measure, measure);
        views.setTextViewText(R.id.tv_quantity, quantity);

        // Fill in the onClick PendingIntent Template using the specific recipe Id for each item individually
        Bundle extras = new Bundle();
        extras.putLong(Config.BUNDLE_RECIPE_ID, recipeId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.item_layout, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        mCursor.moveToPosition(i);
        return Cursors.getLong(mCursor, RecipesColumn.ID);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
