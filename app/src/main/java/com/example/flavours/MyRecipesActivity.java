package com.example.flavours;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MyRecipesActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private List<Recipe> myRecipes;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private RecipeDatabase database;
    private ConstraintLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_recipes);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setSelectedItemId(R.id.my_recipes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                Intent intent;

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MyRecipesActivity.this, Pair.create((View) toolbar, "toolbar"), Pair.create((View) bottomNavigationView, "navigation"));
                switch (id) {
                    case R.id.categories:
                        intent = new Intent(MyRecipesActivity.this, MainActivity.class);
                        startActivity(intent, options.toBundle());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);

                        break;

                    case R.id.explore:
                        intent = new Intent(MyRecipesActivity.this, ExploreActivity.class);
                        startActivity(intent, options.toBundle());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);

                        break;

                    case R.id.my_recipes:
                        break;

                    default:
                }
                return false;
            }
        });

        rootView = findViewById(R.id.root_view);

        database = Room.databaseBuilder(this, RecipeDatabase.class, getString(R.string.db_name)).allowMainThreadQueries().build();
        myRecipes = database.recipeDao().getAllRecipes();

        // Setting recycler view
        recyclerView = findViewById(R.id.my_recipes_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new RecipeAdapter(this, myRecipes);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Recipe current = myRecipes.get(position);

                myRecipes.remove(position);
                adapter.notifyItemRemoved(position);

                Snackbar snackbar = Snackbar.make(rootView, "Recipe deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRecipes.add(position, current);
                        adapter.notifyItemInserted(position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {

                            RecipeWithIngredients recipeWithIngredients = database.recipeDao().getRecipeWithIngredients(current.recipeId);
                            Recipe recipe = recipeWithIngredients.recipe;
                            List<Ingredient> ingredientsOfRecipe = recipeWithIngredients.ingredients;

                            database.recipeDao().deleteRecipe(recipe);

                            for (Ingredient ingredient : ingredientsOfRecipe) {
                                database.recipeDao().deleteIngredient(ingredient);
                            }
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        });

        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RecipeDetailActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<Recipe> newRecipes = database.recipeDao().getAllRecipes();
                adapter.notifyDataSetUpdated(newRecipes);

                Snackbar.make(rootView, R.string.save_message, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(rootView, R.string.discard_message, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void launchDetailActivity(View view) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        startActivityForResult(intent, RecipeDetailActivity.REQUEST_CODE);
    }
}