package com.example.flavours.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.flavours.models.Ingredient;
import com.example.flavours.models.Recipe;

@Database(entities = {Recipe.class, Ingredient.class}, version = 1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
}
