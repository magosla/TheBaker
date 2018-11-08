package com.naijaplanet.magosla.android.thebaker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.library.baseAdapters.BR;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.naijaplanet.magosla.android.thebaker.data.provider.IngredientsColumn;
import com.naijaplanet.magosla.android.thebaker.databinding.IngredientItemBinding;
import com.naijaplanet.magosla.android.thebaker.data.models.Ingredient;

import net.simonvt.schematic.Cursors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private Cursor mCursor;
    private final Context mContext;

    public IngredientsAdapter(Context context) {
        mCursor = null;
        mContext = context;

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
        mCursor.moveToPosition(position);
        View view = holder.itemView;

        FlexboxLayoutManager.LayoutParams lp = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
        lp.setFlexGrow(1);
        view.setLayoutParams(lp);
        holder.bind(mCursor);
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return Cursors.getLong(mCursor, IngredientsColumn.ID);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }


     static class ViewHolder extends RecyclerView.ViewHolder {
        final IngredientItemBinding mBinding;

        ViewHolder(@NonNull IngredientItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            IngredientItemBinding binding = IngredientItemBinding.inflate(inflater, parent, false);

            return new ViewHolder(binding);
        }

         void bind(Cursor cursor) {
            //long ingredientId = Cursors.getLong(cursor, IngredientsColumn.ID);

            Ingredient ingredient = new Ingredient();
            ingredient.setIngredient(Cursors.getString(cursor, IngredientsColumn.INGREDIENT));
            ingredient.setMeasure(Cursors.getString(cursor, IngredientsColumn.MEASURE));
            ingredient.setQuantity(Cursors.getInt(cursor, IngredientsColumn.QUANTITY));

            mBinding.setVariable(BR.ingredient, ingredient);
        }
    }
}
