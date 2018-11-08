package com.naijaplanet.magosla.android.thebaker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.databinding.ActivityRecipesBinding;
import com.naijaplanet.magosla.android.thebaker.ui.fragments.RecipesFragment;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;
import com.naijaplanet.magosla.android.thebaker.utils.RecipeIdlingResource;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

@SuppressWarnings("FieldCanBeLocal")
public class RecipesActivity extends AppCompatActivity implements RecipesFragment.OnFragmentInteractionListener {
    private ActivityRecipesBinding mBinding;
    private RecipeIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public RecipeIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new RecipeIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.enableTransition(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes);

        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);


        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.content.flFragmentHolder.getId(), RecipesFragment.newInstance())
                .commit();
    }


    @Override
    public void onItemClicked(long recipeId) {
        Intent intent = new Intent(this, RecipeActivity.class);
        Log.i(this.getClass().getName(), "The id is " + recipeId);
        intent.putExtra(Config.BUNDLE_RECIPE_ID, recipeId);
        startActivity(intent);
    }

    @Override
    public void onContentLoader() {
        if (mIdlingResource != null)
            mIdlingResource.setIdleState(true);
    }
}
