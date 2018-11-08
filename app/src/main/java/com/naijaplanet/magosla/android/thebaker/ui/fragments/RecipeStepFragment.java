package com.naijaplanet.magosla.android.thebaker.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.naijaplanet.magosla.android.thebaker.Config;
import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.databinding.FragmentRecipeStepBinding;
import com.naijaplanet.magosla.android.thebaker.data.models.Recipe;
import com.naijaplanet.magosla.android.thebaker.data.models.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.ui.activities.ToolbarManager;
import com.naijaplanet.magosla.android.thebaker.utils.ActivityUtil;
import com.naijaplanet.magosla.android.thebaker.utils.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeStepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@SuppressWarnings("ConstantConditions")
public class RecipeStepFragment extends Fragment implements MediaPlayer.Callback {

    private RecipeStep mStep;
    private int mStepsCount;
    private boolean mRecalculateVideoDimension;
    private OnFragmentInteractionListener mListener;

    private FragmentRecipeStepBinding mBinding;
    private MediaPlayer mMediaPlayer;
    private boolean mLandscapeView;
    private boolean mTwoSidedView;


    public RecipeStepFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param step      {@link RecipeStep}
     * @param stepCount The total steps in the {@link Recipe}
     * @return A new instance of fragment RecipeStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeStepFragment newInstance(RecipeStep step, int stepCount) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Config.BUNDLE_RECIPE_STEP, step);
        args.putInt(Config.BUNDLE_RECIPE_STEP_NO, stepCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mStep = args.getParcelable(Config.BUNDLE_RECIPE_STEP);
            mStepsCount = args.getInt(Config.BUNDLE_RECIPE_STEP_NO);
        }
        mTwoSidedView = getResources().getBoolean(R.bool.has_two_pane);
        mLandscapeView = getResources().getBoolean(R.bool.landscape_orientation);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentRecipeStepBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    /**
     * Used to update the UI from the Activity
     *
     * @param step the {{@link RecipeStep}}
     */
    public void updateRecipeStep(RecipeStep step) {
        if(step != null) {
            mStep = step;
            updateUI();
            initializeMediaPlayer();
        }else{
            Toast.makeText(getContext(), "Cannot change step", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        String mediaUrl = !TextUtils.isEmpty(mStep.getVideoURL())?mStep.getVideoURL():mStep.getThumbnailURL();
        mStep.setVideoURL(mediaUrl); // in situations where the thumbnailUrl has the video

        mBinding.setVariable(BR.recipeStep, mStep);
        mBinding.setVariable(BR.recipeStepsCount, mStepsCount);
        mBinding.setVariable(BR.twoSidedView, mTwoSidedView);
        ToolbarManager toolbarManager = (getActivity() instanceof ToolbarManager) ? (ToolbarManager) getActivity() : null;

        if (toolbarManager != null && !TextUtils.isEmpty(mStep.getShortDescription())) {
            toolbarManager.setWindowSubTitle(mStep.getShortDescription());
        }
        mRecalculateVideoDimension = false;

        if (!TextUtils.isEmpty(mediaUrl)) {
            if (mLandscapeView && !mTwoSidedView) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                if (toolbarManager != null) {
                    toolbarManager.displayWindowToolbar(false);
                }

                // this is used to get the dimensions of the screen window
                ActivityUtil.Dimensions dimen = ActivityUtil.getScreenDimension(getActivity());
                mBinding.playerView.setLayoutParams(new FrameLayout.LayoutParams(dimen.width, dimen.height));
                mBinding.playerFrame.setLayoutChangedListener(null);

            }else{
                mRecalculateVideoDimension = true;
                mBinding.playerFrame.setLayoutChangedListener((changed, left, top, right, bottom) -> {
                    if(!mRecalculateVideoDimension)return;

                    ActivityUtil.Dimensions dimen = ActivityUtil.getScreenDimension(getActivity());
                    int height, width = mBinding.playerFrame.getWidth();
                    if(dimen.width < dimen.height){
                        height = width*dimen.width/dimen.height;
                    }else{
                        height = width*dimen.height/dimen.width;
                    }

                    if(width <= 0) return;
                    mRecalculateVideoDimension = false;
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                    mBinding.playerView.setLayoutParams(layoutParams);
                });
            }
        }
        mBinding.actionNextStep.setOnClickListener(view -> {
            if (mListener != null)
                mListener.moveToStep(mStep.getStepNo() + 1);
        });
        mBinding.actionPrevStep.setOnClickListener(view -> {
            if (mListener != null)
                mListener.moveToStep(mStep.getStepNo() - 1);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMediaPlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initializeMediaPlayer() {

        if(!TextUtils.isEmpty(mStep.getVideoURL())) {
            Uri mediaUri = Uri.parse(mStep.getVideoURL());
            // try to release the media player if previously exists
            releaseMediaPlayer();
            mMediaPlayer = new MediaPlayer(getContext(), mBinding.playerView, mediaUri, this);
        }else{
            releaseMediaPlayer();
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void showProgressBar() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void playbackReady() {
        hideProgressBar();
    }

    @Override
    public void playbackBuffering() {
        showProgressBar();
    }



    public interface OnFragmentInteractionListener {
        void moveToStep(int currentStep);
    }

}
