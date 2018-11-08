package com.naijaplanet.magosla.android.thebaker.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.data.models.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeProvider;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeStepsColumn;
import com.naijaplanet.magosla.android.thebaker.databinding.ActivityRecipeStepBinding;
import com.naijaplanet.magosla.android.thebaker.ui.fragments.RecipeStepFragment;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;

import net.simonvt.schematic.Cursors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class RecipeStepActivity extends AppCompatActivity implements ToolbarManager,
        RecipeStepFragment.OnFragmentInteractionListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_NAME = "recipe_name";

    private int mTotalSteps;
    private RecipeStep mStep;
    private int mStepNo;
    private ActivityRecipeStepBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtil.enableTransition(this);

        // if its on double pain exit activity
        if (getResources().getBoolean(R.bool.has_two_pane)) {
            finish();
        }

        Bundle arg = getIntent().getExtras();
        if (arg != null) {

            mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_step);
            setSupportActionBar(mBinding.toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                String title = arg.getString(EXTRA_NAME, "");
                if (!title.isEmpty()) {
                    setTitle(title);
                }
            }


            mTotalSteps = arg.getInt(Config.BUNDLE_RECIPE_STEP_NO, 0);

            if (savedInstanceState != null && savedInstanceState.containsKey(Config.BUNDLE_RECIPE_STEP)) {
                mStep = savedInstanceState.getParcelable(Config.BUNDLE_RECIPE_STEP);
            } else {
                mStep = arg.getParcelable(Config.BUNDLE_RECIPE_STEP);
            }
            loadFragment(mStep);
        }else{
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mStep != null) {
            outState.putParcelable(Config.BUNDLE_RECIPE_STEP, mStep);
        }
    }

    private void loadFragment(RecipeStep step) {
        getSupportFragmentManager().beginTransaction()
                .replace(mBinding.content.flStepContainer.getId(), RecipeStepFragment.newInstance(step, mTotalSteps))
                .commit();
    }

    @Override
    public void setWindowTitle(CharSequence title) {
        setTitle(title);
    }

    @Override
    public void setWindowSubTitle(CharSequence title) {
        mBinding.toolbar.setSubtitle(title);
    }

    @Override
    public void displayWindowToolbar(boolean display) {
        mBinding.toolbar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void moveToStep(int stepNo) {
        mStepNo = stepNo;
        LoaderManager.getInstance(this).initLoader((mStep.getRecipeId() + "" + stepNo).hashCode(), null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] selectionArgs = {String.valueOf(mStep.getRecipeId()),
                String.valueOf(mStepNo)};

        return new CursorLoader(this, RecipeProvider.RecipeSteps.CONTENT_URI
                , null,
                RecipeStepsColumn.RECIPE_ID + "=? AND " + RecipeStepsColumn.STEP_NO + "=?",
                selectionArgs, RecipeStepsColumn.STEP_NO);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        Log.i(this.getClass().getName(), "step no when load finished " + mStepNo);
        RecipeStep step = null;
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            step = new RecipeStep();
            step.setId((int) Cursors.getLong(data, RecipeStepsColumn.ID));
            step.setRecipeId((int) Cursors.getLong(data, RecipeStepsColumn.RECIPE_ID));
            step.setDescription(Cursors.getString(data, RecipeStepsColumn.DESCRIPTION));
            step.setShortDescription(Cursors.getString(data, RecipeStepsColumn.SHORT_DESCRIPTION));
            step.setThumbnailURL(Cursors.getString(data, RecipeStepsColumn.THUMBNAIL));
            step.setVideoURL(Cursors.getString(data, RecipeStepsColumn.VIDEO_URL));
            step.setStepNo(Cursors.getInt(data, RecipeStepsColumn.STEP_NO));
            mStep = step;
            Log.i(this.getClass().getName(), "step no when " + step.getStepNo());

            // loadFragment(step);
        }

        RecipeStepFragment fragment = (RecipeStepFragment) getSupportFragmentManager().
                findFragmentById(mBinding.content.flStepContainer.getId());
        if (fragment != null) {
            fragment.updateRecipeStep(step);
        }

        //loadFragment(step);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        RecipeStepFragment fragment = (RecipeStepFragment) getSupportFragmentManager().
                findFragmentById(mBinding.content.flStepContainer.getId());
        if (fragment != null) {
            fragment.updateRecipeStep(null);
        }
    }
}
