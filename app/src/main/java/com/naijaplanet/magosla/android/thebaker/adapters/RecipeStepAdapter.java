package com.naijaplanet.magosla.android.thebaker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.data.models.RecipeStep;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipeStepsColumn;
import com.naijaplanet.magosla.android.thebaker.databinding.RecipeStepListItemBinding;
import com.naijaplanet.magosla.android.thebaker.ui.fragments.RecipeStepFragment;

import net.simonvt.schematic.Cursors;


import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> implements RecipeStepFragment.OnFragmentInteractionListener {

    private static int mItemCount;
    private static AdapterItemInteraction mListener;
    private final Context mContext;
    private Cursor mCursor;

    public RecipeStepAdapter(Context context, AdapterItemInteraction listener) {
        mContext = context;
        mCursor = null;

        mListener = listener;

        setHasStableIds(true);
    }

    @Override
    public void moveToStep(int currentStep) {

    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        mItemCount = getItemCount();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.create(LayoutInflater.from(mContext), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bind(mCursor);
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return Cursors.getLong(mCursor, RecipeStepsColumn.ID);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    public interface AdapterItemInteraction {
        @SuppressWarnings("unused")
        void itemClicked(int position, RecipeStep recipeStep, int recipeStepNo, View clickedItem);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final RecipeStepListItemBinding mBinding;

        private ViewHolder(@NonNull RecipeStepListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }

        private static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            RecipeStepListItemBinding binding = RecipeStepListItemBinding.inflate(inflater, parent, false);

            return new ViewHolder(binding);
        }

        void bind(Cursor cursor) {
            RecipeStep step = new RecipeStep();
            step.setId((int) Cursors.getLong(cursor, RecipeStepsColumn.ID));
            step.setRecipeId((int) Cursors.getLong(cursor, RecipeStepsColumn.RECIPE_ID));
            step.setDescription(Cursors.getString(cursor, RecipeStepsColumn.DESCRIPTION));
            step.setShortDescription(Cursors.getString(cursor, RecipeStepsColumn.SHORT_DESCRIPTION));
            step.setThumbnailURL(Cursors.getString(cursor, RecipeStepsColumn.THUMBNAIL));
            step.setVideoURL(Cursors.getString(cursor, RecipeStepsColumn.VIDEO_URL));
            step.setStepNo(Cursors.getInt(cursor, RecipeStepsColumn.STEP_NO));

            mBinding.setVariable(BR.step, step);

            mBinding.tvShortDescription.setText(step.getShortDescription());
            itemView.setTag(R.string.tag_key_step_object, step);
            //itemView.setActivated(false);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                RecipeStep step;
                step = (RecipeStep) view.getTag(R.string.tag_key_step_object);

                mListener.itemClicked(getAdapterPosition(), step, mItemCount, view);
            }
        }
    }
}
