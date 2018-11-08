package com.naijaplanet.magosla.android.thebaker.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.adapters.IngredientsAdapter;
import com.naijaplanet.magosla.android.thebaker.adapters.RecipeStepAdapter;
import com.naijaplanet.magosla.android.thebaker.data.models.Recipe;
import com.naijaplanet.magosla.android.thebaker.data.models.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.data.provider.IngredientsColumn;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeProvider;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeStepsColumn;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipesColumn;
import com.naijaplanet.magosla.android.thebaker.databinding.FragmentRecipeBinding;
import com.naijaplanet.magosla.android.thebaker.ui.activities.ToolbarManager;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;
import com.naijaplanet.magosla.android.thebaker.utils.SpaceItemDecoration;

import net.simonvt.schematic.Cursors;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, RecipeStepAdapter.AdapterItemInteraction {
    private static final String ARG_RECIPE_ID = "param1";

    private static final int LOADER_ID_RECIPE_STEPS = 123;

    private static final int LOADER_ID_INGREDIENTS = 124;
    private static final String BUNDLE_CHOICE_STEP = "choice_step";

    private static final int LOADER_ID_RECIPE = 125;

    private long mRecipeId;

    private Recipe mRecipe;
    
    private Context mContext;
    
    private int mSelectedItemAdapterPosition=-1;

    private RecipeStepAdapter mStepAdapter;
    private IngredientsAdapter mIngredientAdapter;

    private FragmentRecipeBinding mBinding;

    private OnFragmentInteractionListener mListener;

    private ToolbarManager mToolbarManager;

    private WeakReference<View> mLastClicked;


    private final RecyclerView.AdapterDataObserver mStepsObserver =
            new RecyclerView.AdapterDataObserver() {

                @Override
                public void onChanged() {
                    handleStepState();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    handleStepState();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    handleStepState();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    handleStepState();
                }
            };


    private final RecyclerView.AdapterDataObserver mIngredientObserver =
            new RecyclerView.AdapterDataObserver() {

                @Override
                public void onChanged() {
                    handleIngredientState();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    handleIngredientState();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    handleIngredientState();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    handleIngredientState();
                }
            };

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipeId the recipe ID
     * @return A new instance of fragment RecipeFragment.
     */
    public static RecipeFragment newInstance(long recipeId) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    private void handleStepState() {
        int itemCount = mStepAdapter.getItemCount();
        mBinding.loadingSteps.setVisibility(View.GONE);
        mBinding.emptySteps.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    private void handleIngredientState() {
        int itemCount = mIngredientAdapter.getItemCount();
        mBinding.loadingIngredients.setVisibility(View.GONE);
        mBinding.emptyIngredient.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {
            mRecipeId = getArguments().getLong(ARG_RECIPE_ID);
        }
        mStepAdapter = new RecipeStepAdapter(mContext, this);
        mToolbarManager = (getActivity() instanceof ToolbarManager) ? (ToolbarManager) getActivity() : null;
        if (mToolbarManager != null) {
            mToolbarManager.setWindowTitle(getString(R.string.msg_loading_recipe));
        }
        mIngredientAdapter = new IngredientsAdapter(mContext);
        mRecipe = new Recipe();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentRecipeBinding.inflate(inflater, container, false);

        // see if any step has been selected before there was a configuration change
        if(savedInstanceState!= null && savedInstanceState.containsKey(BUNDLE_CHOICE_STEP)){
            mSelectedItemAdapterPosition = savedInstanceState.getInt(BUNDLE_CHOICE_STEP);
        }
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setupIngredientsView();

        setupStepsView();

        LoaderManager.getInstance(this).initLoader(LOADER_ID_RECIPE_STEPS, null, this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID_INGREDIENTS, null, this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID_RECIPE, null, this);
    }

    private void setupIngredientsView() {
        RecyclerView recyclerView = mBinding.rvIngredients;
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);

        //DividerItemDecoration decoration = new DividerItemDecoration(mContext, HORIZONTAL);
        SpaceItemDecoration decoration = new SpaceItemDecoration(10);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(mIngredientAdapter);

        mIngredientAdapter.registerAdapterDataObserver(mIngredientObserver);
    }

    private void setupStepsView() {
        RecyclerView recyclerView = mBinding.rvRecipeSteps;
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(mStepAdapter);
        mStepAdapter.registerAdapterDataObserver(mStepsObserver);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] selectionArgs = {String.valueOf(mRecipeId)};
        CursorLoader loader = null;

        switch (id) {
            case LOADER_ID_RECIPE_STEPS:
                loader = new CursorLoader(getActivity(), RecipeProvider.RecipeSteps.CONTENT_URI
                        , null, RecipeStepsColumn.RECIPE_ID + "=?", selectionArgs, RecipeStepsColumn.STEP_NO);
                break;
            case LOADER_ID_INGREDIENTS:
                loader = new CursorLoader(getActivity(), RecipeProvider.Ingredients.CONTENT_URI, null,
                        IngredientsColumn.RECIPE_ID + "=?", selectionArgs, IngredientsColumn.ID);
                break;
            case LOADER_ID_RECIPE:
                loader = new CursorLoader(getActivity(), RecipeProvider.Recipes.CONTENT_URI, null,
                        RecipesColumn.ID + "=?", selectionArgs, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID_RECIPE_STEPS:
                mStepAdapter.setCursor(data);
                break;
            case LOADER_ID_INGREDIENTS:
                mIngredientAdapter.setCursor(data);
                break;
            case LOADER_ID_RECIPE:
                if (mToolbarManager != null && data != null && data.getCount() > 0) {
                    data.moveToFirst();
                    mRecipe.setServings(Cursors.getInt(data, RecipesColumn.SERVINGS));
                    mRecipe.setName(Cursors.getString(data, RecipesColumn.NAME));
                    mRecipe.setId(Cursors.getInt(data, RecipesColumn.ID));
                    mToolbarManager.setWindowTitle(mRecipe.getName());
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID_RECIPE_STEPS:
                mStepAdapter.setCursor(null);
                break;
            case LOADER_ID_INGREDIENTS:
                mIngredientAdapter.setCursor(null);
                break;
            case LOADER_ID_RECIPE:
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // see if there is a selected step position to save
        if(ActivityUtil.isTabletDevice(mContext) && mSelectedItemAdapterPosition != -1){
            outState.putInt(BUNDLE_CHOICE_STEP, mSelectedItemAdapterPosition);
        }
    }

    @Override
    public void itemClicked(int position, RecipeStep recipeStep, int stepsCount, View clickedItem) {
        // deactivate the last activated item if exist
        if (mLastClicked != null) {
            View lastClicked = mLastClicked.get();
            if (lastClicked != null) {
                lastClicked.setActivated(false);
            }
        }
        clickedItem.setActivated(true);
        mLastClicked = new WeakReference<>(clickedItem);
        mListener.onStepItemClick(recipeStep, stepsCount, clickedItem);
    }

    public interface OnFragmentInteractionListener {
        @SuppressWarnings("unused")
        void onStepItemClick(RecipeStep recipeStep, int stepsCount, View clickedItem);
    }
}
