package com.example.flavours.data.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Ingredient {
    @Ignore
    public static final int INGREDIENT_COUNT = 20;

    @PrimaryKey(autoGenerate = true)
    public long ingredientId;

    @ColumnInfo
    public long recipeParentId;

    @ColumnInfo
    public String ingredient;

    @ColumnInfo
    public String measure;

    public Ingredient(String ingredient, String measure, long recipeParentId) {
        this.ingredient = ingredient;
        this.measure = measure;
        this.recipeParentId = recipeParentId;
    }
}
