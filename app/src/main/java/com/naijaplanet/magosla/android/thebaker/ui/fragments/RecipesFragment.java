package com.naijaplanet.magosla.android.thebaker.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.thebaker.adapters.RecipesAdapter;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeProvider;
import com.naijaplanet.magosla.android.thebaker.databinding.FragmentRecipesBinding;
import com.naijaplanet.magosla.android.thebaker.receivers.RecipeServiceReceiver;
import com.naijaplanet.magosla.android.thebaker.services.RecipeService;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;
import com.naijaplanet.magosla.android.thebaker.utils.GridItemSpacingDecoration;
import com.naijaplanet.magosla.android.thebaker.utils.GridItemsSpanSpacing;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings({"FieldCanBeLocal", "NullableProblems"})
public class RecipesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, RecipesAdapter.AdapterItemInteraction {


    private static final int LOADER_ID_RECIPES = 22;

    private static final int GRID_SPAN = 3;

    private OnFragmentInteractionListener mListener;

    private FragmentRecipesBinding mBinding;


    private RecipesAdapter mAdapter;
    private final RecyclerView.AdapterDataObserver  mAdapterObserver =
            new RecyclerView.AdapterDataObserver() {

                @Override
                public void onChanged() {
                    setPlaceHolderVisibility();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    setPlaceHolderVisibility();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    setPlaceHolderVisibility();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    setPlaceHolderVisibility();
                }
            };

    public RecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipesFragment.
     */
    public static RecipesFragment newInstance() {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getContext();
        if(context!=null) {
            mAdapter = new RecipesAdapter(getContext(), this);
            mAdapter.registerAdapterDataObserver(mAdapterObserver);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRecipesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = mBinding.rvRecipes;
        Context context = getContext();
        Activity activity = getActivity();
        if (context != null && activity!=null) {

            if (ActivityUtil.isTabletDevice(context)) {
                int gridSpan = ActivityUtil.isLandScapeView(context) ? GRID_SPAN : GRID_SPAN - 1;
                GridLayoutManager layoutManager = new GridLayoutManager(context, gridSpan);
                recyclerView.setLayoutManager(layoutManager);

                ActivityUtil.Dimensions dimen = ActivityUtil.getScreenDimension(activity);
                int item_width = (dimen.width / gridSpan) - (8 * (gridSpan + 1));

                GridItemsSpanSpacing gridItemsSpanSpacing = new GridItemsSpanSpacing(mBinding.rvRecipes,
                        item_width, 0, layoutManager.getOrientation(), true, false);

                mBinding.rvRecipes.addItemDecoration(new GridItemSpacingDecoration(gridItemsSpanSpacing));

            } else {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                DividerItemDecoration decoration = new DividerItemDecoration(context, VERTICAL);
                recyclerView.addItemDecoration(decoration);
            }
        }
        recyclerView.setAdapter(mAdapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID_RECIPES, null, this);
    }

    /**
     * Indicator for no internet connection
     */
    private void noInternet() {
        mBinding.msgNoNetwork.setVisibility(View.VISIBLE);
    }

    /**
     * Task to perform when the online recipe fetch we unsuccessful
     */
    private void fetchError() {
        mBinding.actionReload.setVisibility(View.VISIBLE);
        mBinding.actionReload.setOnClickListener(view -> updateFromNetwork());
        mListener.onContentLoader();
    }

    /**
     * When the task to get the recipes online completed (failure or success)
     */
    private void loadingEnded() {
        mBinding.pbLoading.setVisibility(View.GONE);
    }

    /**
     * When the task to fetch recipes online begins
     */
    private void loadingRecipe() {
        mBinding.msgNoNetwork.setVisibility(View.GONE);
        mBinding.actionReload.setVisibility(View.GONE);
        mBinding.actionReload.setOnClickListener(null);
        mBinding.pbLoading.setVisibility(View.VISIBLE);
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
        Activity activity = getActivity();
        assert activity != null;
        return new CursorLoader(activity, RecipeProvider.Recipes.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            // the database is empty, fetch the records from the web
            Context context = getContext();
            if (context == null) {
                return;
            }
            updateFromNetwork();
        } else {
            mAdapter.setCursor(data);

            mListener.onContentLoader();
        }
    }

    private void updateFromNetwork() {
        Context context = getContext();
        if (context == null) return;
        RecipeService.updateRecipeDatabase(context, new RecipeNetworkResponse(this));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    private void setPlaceHolderVisibility() {
        int itemCount = mAdapter.getItemCount();
        mBinding.tvNoContent.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void itemClicked(long recipeId) {
        if (mListener != null) {
            mListener.onItemClicked(recipeId);
        }
    }

    public interface OnFragmentInteractionListener {
        void onItemClicked(long recipeId);
        void onContentLoader();
    }


    private static class RecipeNetworkResponse implements RecipeServiceReceiver.Callback {

        @SuppressWarnings("CanBeFinal")
        private WeakReference<RecipesFragment> mFragmentRef;

        private RecipeNetworkResponse(RecipesFragment fragment) {
            mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void onComplete(RecipeServiceReceiver.Response response) {
            if (mFragmentRef != null) {
                mFragmentRef.get().loadingEnded();
                if (response == RecipeServiceReceiver.Response.NO_INTERNET) {
                    mFragmentRef.get().noInternet();
                }
                if (response != RecipeServiceReceiver.Response.SUCCESS) {
                    mFragmentRef.get().fetchError();
                }
            }
        }

        @Override
        public void onStart() {
            if (mFragmentRef != null) {
                mFragmentRef.get().loadingRecipe();
            }
        }
    }
}
