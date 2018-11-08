package com.naijaplanet.magosla.android.thebaker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naijaplanet.magosla.android.thebaker.R;
import androidx.databinding.library.baseAdapters.BR;
import com.naijaplanet.magosla.android.thebaker.data.provider.RecipesColumn;
import com.naijaplanet.magosla.android.thebaker.databinding.RecipeListItemBinding;
import com.naijaplanet.magosla.android.thebaker.data.models.Recipe;

import net.simonvt.schematic.Cursors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private static AdapterItemInteraction mListener;

    public interface AdapterItemInteraction{
        void itemClicked(long recipeId);
    }

    public RecipesAdapter(@NonNull Context context, AdapterItemInteraction listener) {
        mContext = context;
        mListener = listener;
        setHasStableIds(true);

    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.create(LayoutInflater.from(mContext), parent);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(RecipesAdapter.class.getName(), "adapter position: "+ position);
        mCursor.moveToPosition(position);
        holder.bind(mCursor);
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return Cursors.getLong(mCursor, RecipesColumn.ID);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        final RecipeListItemBinding mBinding;

        private ViewHolder(@NonNull RecipeListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            RecipeListItemBinding binding = RecipeListItemBinding.inflate(inflater, parent, false);

            return new ViewHolder(binding);
        }

        void bind(Cursor cursor) {
            long recipeId = Cursors.getLong(cursor, RecipesColumn.ID);

            Recipe recipe = new Recipe();
            recipe.setId(Cursors.getInt(cursor, RecipesColumn.ID));
            recipe.setName(Cursors.getString(cursor, RecipesColumn.NAME));
            recipe.setServings(Cursors.getInt(cursor, RecipesColumn.SERVINGS));

            mBinding.setVariable(BR.recipe, recipe);

            mBinding.setVariable(BR.icon_letter, String.valueOf(recipe.getName().charAt(0)));

            itemView.setOnClickListener(this);
            itemView.setTag(R.string.tag_key_recipe_id, recipeId);
        }


        @Override
        public void onClick(View view) {
            if(mListener != null){
                long recipeId = (long)view.getTag(R.string.tag_key_recipe_id);
                mListener.itemClicked(recipeId);
            }
        }
    }
}
