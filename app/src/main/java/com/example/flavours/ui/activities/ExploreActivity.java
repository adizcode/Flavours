package com.example.flavours.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.flavours.R;
import com.example.flavours.ui.adapters.RecipeAdapter;
import com.example.flavours.data.models.Ingredient;
import com.example.flavours.data.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This Activity is launched when the user taps on the Explore Tab, or when the user taps on a category on the Categories Tab.
 * It displays a SearchView and a RecyclerView.
 * The SearchView makes API calls to TheMealDB API to request Recipes matching the query.
 * The resultant Recipes are displayed in the RecyclerView as a list of CardViews.
 */
public class ExploreActivity extends AppCompatActivity {

    // String constants used in this Activity
    private static final String LOG_TAG = ExploreActivity.class.getSimpleName();
    private static final String SEARCH_TAG = "search_request";
    private static final String SEARCH_URL = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
    private static final String TYPE_URL = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";
    private static final String MAIN_COURSE_URL = "https://www.themealdb.com/api/json/v1/1/filter.php?a=Indian";
    private static final String LOOKUP_BY_ID_URL = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=";

    // Toolbar and bottom navigation bar
    // Identical to the ones used in MainActivity and MyRecipesActivity
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    // RequestQueue
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explore);

        // Finding a reference to the toolbar and setting it as the action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Finding a reference to the bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        // Changing the selected tab in the bottom navigation bar.
        bottomNavigationView.setSelectedItemId(R.id.explore);

        // Setting up the bottom navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            Intent intent;

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ExploreActivity.this, Pair.create(toolbar, "toolbar"), Pair.create(bottomNavigationView, "navigation"));

            // TODO: Convert switch statement to if
            switch (id) {
                case R.id.categories:
                    intent = new Intent(ExploreActivity.this, MainActivity.class);
                    startActivity(intent, options.toBundle());

                    new Handler().postDelayed(this::finish, 2000);

                    break;

                case R.id.explore:
                    break;

                case R.id.my_recipes:
                    intent = new Intent(ExploreActivity.this, MyRecipesActivity.class);
                    startActivity(intent, options.toBundle());

                    new Handler().postDelayed(this::finish, 2000);

                    break;

                default:
            }
            return false;
        });

        // Enabling Volley debug messages
        VolleyLog.DEBUG = true;

        // Creating a new RequestQueue
        queue = Volley.newRequestQueue(this);

        // Data set for the RecyclerView.Adapter
        List<Recipe> dataSet = new ArrayList<>();


        // Setting up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.explore_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        final RecipeAdapter adapter = new RecipeAdapter(this, dataSet);
        recyclerView.setAdapter(adapter);

        // Finding a reference to the search view and setting it up
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queue.cancelAll(SEARCH_TAG);

                final List<Recipe> filteredList = new ArrayList<>();

                JsonObjectRequest recipesRequest = new JsonObjectRequest(Request.Method.GET, SEARCH_URL + newText, null, response -> {
                    try {

                        JSONArray meals = response.getJSONArray("meals");

                        for (int i = 0; i < meals.length(); i++) {
                            JSONObject meal = meals.getJSONObject(i);
                            String name = meal.getString("strMeal");
                            String strCat = meal.getString("strCategory");
                            String category = strCat.equals("Side") ? "Side Dish" : strCat;
                            String area = meal.getString("strArea");
                            String instructions = meal.getString("strInstructions");
                            String thumbUrl = meal.getString("strMealThumb");
                            String youtubeUrl = meal.getString("strYoutube");

                            List<String> ingredients = new ArrayList<>();
                            List<String> ingredientMeasures = new ArrayList<>();

                            for (int j = 1; j <= Ingredient.INGREDIENT_COUNT; j++) {
                                String ingredient = meal.getString("strIngredient" + j).trim();
                                String ingredientMeasure = meal.getString("strMeasure" + j).trim();

                                if (ingredient.length() == 0 || ingredientMeasure.length() == 0) {
                                    break;
                                }

                                ingredients.add(ingredient);
                                ingredientMeasures.add(ingredientMeasure);
                            }

                            Recipe recipe = new Recipe(name, category, area, instructions, thumbUrl, youtubeUrl, ingredients, ingredientMeasures, true);
                            filteredList.add(recipe);

                            // Checks end of loop
                            if (i == meals.length() - 1) {
                                adapter.notifyDataSetUpdated(filteredList);
                            }

                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON Error", e);
                    }
                }, error -> VolleyLog.d(LOG_TAG, error));

                recipesRequest.setTag(SEARCH_TAG);
                queue.add(recipesRequest);

                return true;
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.RECIPE_CAT)) {
            String category = intent.getStringExtra(MainActivity.RECIPE_CAT);

            if (category != null) {

                queue.cancelAll(SEARCH_TAG);

                if (category.equalsIgnoreCase("Salad")) {

                    final List<Recipe> filteredList = new ArrayList<>();

                    JsonObjectRequest recipesRequest = new JsonObjectRequest(Request.Method.GET, SEARCH_URL + category, null, response -> {
                        try {
                            JSONArray meals = response.getJSONArray("meals");

                            for (int i = 0; i < meals.length(); i++) {
                                JSONObject meal = meals.getJSONObject(i);
                                String name = meal.getString("strMeal");
                                String strCat = meal.getString("strCategory");
                                String category1 = strCat.equals("Side") ? "Side Dish" : strCat;
                                String area = meal.getString("strArea");
                                String instructions = meal.getString("strInstructions");
                                String thumbUrl = meal.getString("strMealThumb");
                                String youtubeUrl = meal.getString("strYoutube");

                                List<String> ingredients = new ArrayList<>();
                                List<String> ingredientMeasures = new ArrayList<>();

                                for (int j = 1; j <= Ingredient.INGREDIENT_COUNT; j++) {
                                    String ingredient = meal.getString("strIngredient" + j).trim();
                                    String ingredientMeasure = meal.getString("strMeasure" + j).trim();

                                    if (ingredient.length() == 0 || ingredientMeasure.length() == 0) {
                                        break;
                                    }

                                    ingredients.add(ingredient);
                                    ingredientMeasures.add(ingredientMeasure);
                                }

                                Recipe recipe = new Recipe(name, category1, area, instructions, thumbUrl, youtubeUrl, ingredients, ingredientMeasures, true);
                                filteredList.add(recipe);

                                // Checks end of loop
                                if (i == meals.length() - 1) {
                                    adapter.notifyDataSetUpdated(filteredList);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "JSON Error", e);
                        }
                    }, error -> VolleyLog.d(LOG_TAG, error));

                    recipesRequest.setTag(SEARCH_TAG);
                    queue.add(recipesRequest);
                } else {
                    String url;

                    if (category.equalsIgnoreCase("Indian")) {
                        url = MAIN_COURSE_URL;
                    } else {
                        url = TYPE_URL + category;
                    }

                    final List<Recipe> filteredList = new ArrayList<>();

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                        try {

                            JSONArray meals = response.getJSONArray("meals");

                            for (int i = 0; i < meals.length(); i++) {
                                JSONObject meal = meals.getJSONObject(i);
                                String id = meal.getString("idMeal");

                                String lookupUrl = LOOKUP_BY_ID_URL + id;

                                JsonObjectRequest detail_request = new JsonObjectRequest(Request.Method.GET, lookupUrl, null, response1 -> {
                                    try {
                                        JSONObject mealLookedUp = response1.getJSONArray("meals").getJSONObject(0);

                                        String name = mealLookedUp.getString("strMeal");
                                        String strCat = mealLookedUp.getString("strCategory");
                                        String cat = strCat.equals("Side") ? "Side Dish" : strCat;
                                        String area = mealLookedUp.getString("strArea");
                                        String instructions = mealLookedUp.getString("strInstructions");
                                        String thumbUrl = mealLookedUp.getString("strMealThumb");
                                        String youtubeUrl = mealLookedUp.getString("strYoutube");

                                        List<String> ingredients = new ArrayList<>();
                                        List<String> ingredientMeasures = new ArrayList<>();

                                        for (int j = 1; j <= Ingredient.INGREDIENT_COUNT; j++) {
                                            String ingredient = mealLookedUp.getString("strIngredient" + j).trim();
                                            String ingredientMeasure = mealLookedUp.getString("strMeasure" + j).trim();

                                            if (ingredient.length() == 0 || ingredientMeasure.length() == 0) {
                                                break;
                                            }

                                            ingredients.add(ingredient);
                                            ingredientMeasures.add(ingredientMeasure);
                                        }

                                        Recipe recipe = new Recipe(name, cat, area, instructions, thumbUrl, youtubeUrl, ingredients, ingredientMeasures, true);
                                        filteredList.add(recipe);

                                        adapter.notifyDataSetUpdated(filteredList);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> VolleyLog.d(LOG_TAG, error));

                                Volley.newRequestQueue(getApplicationContext()).add(detail_request);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }, error -> VolleyLog.d(LOG_TAG, error));

                    request.setTag(SEARCH_TAG);
                    queue.add(request);
                }

            }
        }

    }
}