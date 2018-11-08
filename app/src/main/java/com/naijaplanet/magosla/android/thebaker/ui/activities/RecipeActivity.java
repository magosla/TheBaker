package com.naijaplanet.magosla.android.thebaker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.data.models.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.databinding.ActivityRecipeBinding;
import com.naijaplanet.magosla.android.thebaker.ui.fragments.RecipeFragment;
import com.naijaplanet.magosla.android.thebaker.ui.fragments.RecipeStepFragment;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.OnFragmentInteractionListener, ToolbarManager {

    private static final String FRAGMENT_MAIN_TAG = "main_frag_tag";
    private int mRecipeId;
    private ActivityRecipeBinding mBinding;
    private boolean mHasTwoPane;
    private String mRecipeName;
    private boolean mRecipeSelected;
    private final int MenuItem_Recipe_Select = 11;
    private Toast mToast;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_recipe, menu);

        MenuItem item = menu.add(Menu.NONE, MenuItem_Recipe_Select, Menu.NONE,mRecipeSelected?R.string.label_remove:R.string.label_set);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        if (mRecipeSelected) {
            item.setIcon(R.drawable.ic_remove);
        }else{
            item.setIcon(R.drawable.ic_add);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MenuItem_Recipe_Select:
                if (!mRecipeSelected) {
                    item.setIcon(R.drawable.ic_remove);
                    item.setTitle(R.string.label_remove);

                    Config.saveUsersRecipe(getApplicationContext(), mRecipeId);
                    mRecipeSelected = true;
                    showToast(getString(R.string.msg_recipe_selected));
                }else{
                    item.setIcon(R.drawable.ic_add);
                    item.setTitle(R.string.label_set);

                    Config.clearUserRecipe(getApplicationContext());
                    mRecipeSelected = false;
                    showToast(getString(R.string.msg_recipe_un_select));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String text) {
        if(mToast!=null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.enableTransition(this);

        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            mRecipeId = (int) arg.getLong(Config.BUNDLE_RECIPE_ID, 0);
            if (mRecipeId == 0) {
                Toast.makeText(this, R.string.msg_invalid_id_arg, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            finish();
        }

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe);

        mHasTwoPane = (mBinding.content.flRecipeStepContainer != null);

        mRecipeSelected = Config.isUserRecipe(this, mRecipeId);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // setTitle();
        }

        updateUI();

    }

    private void updateUI() {
        FragmentManager fm = getSupportFragmentManager();
        RecipeFragment rf = (RecipeFragment) fm.findFragmentByTag(FRAGMENT_MAIN_TAG);
        if (rf == null) {
            fm.beginTransaction().add(mBinding.content.flRecipeStepsContainer.getId(),
                    RecipeFragment.newInstance(mRecipeId), FRAGMENT_MAIN_TAG).commit();
        }
    }


    @Override
    public void onStepItemClick(RecipeStep recipeStep, int recipeStepCount, View itemClicked) {
        if (mHasTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(mBinding.content.flRecipeStepContainer.getId(), RecipeStepFragment.newInstance(recipeStep, recipeStepCount))
                    .commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Config.BUNDLE_RECIPE_STEP, recipeStep);
            bundle.putLong(Config.BUNDLE_RECIPE_ID, mRecipeId);
            bundle.putInt(Config.BUNDLE_RECIPE_STEP_NO, recipeStepCount);
            bundle.putString(RecipeStepActivity.EXTRA_NAME, mRecipeName);

            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void setWindowTitle(CharSequence title) {
        setTitle(title);
        mRecipeName = String.valueOf(title);
    }

    @Override
    public void setWindowSubTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }
}
