package com.example.flavours;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipe WHERE downloaded = 0")
    List<Recipe> getMyRecipes();

    @Query("SELECT * FROM recipe WHERE downloaded = 1")
    List<Recipe> getDownloadedRecipes();

    @Query("SELECT * FROM recipe WHERE recipeName LIKE :recipeName")
    List<Recipe> findRecipesByName(String recipeName);

    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    Recipe findRecipeById(long recipeId);

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    RecipeWithIngredients getRecipeWithIngredients(long recipeId);

    @Insert(entity = Ingredient.class)
    long insertIngredient(Ingredient ingredient);

    @Delete(entity = Ingredient.class)
    void deleteIngredient(Ingredient ingredient);

    @Insert(entity = Recipe.class)
    long insertRecipe(Recipe recipe);

    @Delete(entity = Recipe.class)
    void deleteRecipe(Recipe recipe);

    @Query("UPDATE recipe SET downloaded = 1 WHERE recipeId = :recipeId")
    void setDownloaded(long recipeId);
}
