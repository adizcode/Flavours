package com.example.flavours;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class, Ingredient.class}, version = 1, exportSchema = false)
public abstract class RecipeDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
}
