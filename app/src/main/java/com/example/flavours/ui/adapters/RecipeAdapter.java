package com.example.flavours.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flavours.R;
import com.example.flavours.ui.activities.RecipeDetailActivity;
import com.example.flavours.data.models.Recipe;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final List<Recipe> recipeDataSet;
    private final List<Recipe> recipeDataSetDefault;
    private final Context context;


    public RecipeAdapter(Context context, List<Recipe> recipeDataSet) {
        this.mInflater = LayoutInflater.from(context);
        this.recipeDataSet = recipeDataSet;
        this.context = context;

        this.recipeDataSetDefault = new ArrayList<>(recipeDataSet);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe current = recipeDataSet.get(position);

        if (current.recipeThumbUrl != null) {
            Glide
                    .with(context)
                    .load(current.recipeThumbUrl)
                    .into(holder.recipeImage);
        }
        else if (current.recipeImagePath != null) {
            Bitmap image = BitmapFactory.decodeFile(current.recipeImagePath);
            Glide
                    .with(context)
                    .load(image)
                    .into(holder.recipeImage);
        }

        holder.recipeTitle.setText(current.getRecipeName());
        holder.recipeArea.setText(current.getRecipeArea());
        holder.recipeType.setText(current.getRecipeCategory());
    }

    @Override
    public int getItemCount() {
        return recipeDataSet.size();
    }

    /**
     * Updates the data set of the adapter.
     */
    public void notifyDataSetUpdated(@Nullable List<Recipe> newDataSet) {

        recipeDataSet.clear();

        if (newDataSet != null) {

            recipeDataSet.addAll(newDataSet);
        } else {
            recipeDataSet.addAll(recipeDataSetDefault);
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView recipeImage;
        TextView recipeTitle;
        Chip recipeArea;
        Chip recipeType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_img);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeArea = itemView.findViewById(R.id.recipe_area);
            recipeType = itemView.findViewById(R.id.recipe_type);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            Recipe current = recipeDataSet.get(getLayoutPosition());
            intent.putExtra(RecipeDetailActivity.RECIPE_OBJECT, current);
            ((Activity) context).startActivityForResult(intent, RecipeDetailActivity.REQUEST_CODE);
        }
    }
}
