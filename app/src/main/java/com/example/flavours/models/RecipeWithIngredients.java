package com.example.flavours.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithIngredients {
    @Embedded
    public Recipe recipe;

    @Relation(
            parentColumn = "recipeId",
            entityColumn = "recipeParentId"
    )
    public List<Ingredient> ingredients;
}
