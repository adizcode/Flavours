package com.example.flavours.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.flavours.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

/**
 * This Activity is launched when the app is opened, or when the user taps on the Categories Tab.
 * It displays a grid of clickable CardViews.
 * Each CardView represents a particular food category.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // The implemented OnClickListener is attached to each CardView displayed in the grid

    // Key used to pass category name with the intent
    public static final String RECIPE_CAT = "Category";

    // Toolbar and bottom navigation bar
    // Identical to the ones used in ExploreActivity and MyRecipesActivity
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Finding a reference to the toolbar and setting it as the action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Finding a reference to the bottom navigation bar and setting it up
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            // Declaring empty intent variable
            Intent intent;

            // Creating transition animation for shared toolbar and bottom navigation bar
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create(toolbar, "toolbar"), Pair.create(bottomNavigationView, "navigation"));

            // TODO: Convert switch statement to if
            // Item-dependent behaviour
            switch (item.getItemId()) {
                // TODO: Understand Runnable and Handler
                // TODO: Verify the impact of ActivityOptions.makeSceneTransitionAnimation()

                // Current Tab
                case R.id.categories:
                    break;

                // ExploreActivity Tab
                case R.id.explore:

                    // Launches ExploreActivity with the transition
                    intent = new Intent(MainActivity.this, ExploreActivity.class);
                    startActivity(intent, options.toBundle());

                    // Finishes current Activity after 2 seconds
                    new Handler().postDelayed(this::finish, 2000);

                    break;

                // MyRecipesActivity Tab
                case R.id.my_recipes:

                    // Launches MyRecipesActivity with the transition
                    intent = new Intent(MainActivity.this, MyRecipesActivity.class);
                    startActivity(intent, options.toBundle());

                    // Finishes current Activity after 2 seconds
                    new Handler().postDelayed(this::finish, 2000);

                    break;

                // Empty default case
                default:
            }

            // Should return true to display the clicked item as the selected item
            // In this app, each tab is linked to a separate Activity, albeit with identical bottom navigation bars
            // Hence returning false
            return false;
        });

        // Retrieving category names from resources
        String[] categories = getResources().getStringArray(R.array.categories);

        // Retrieving drawable resource IDs for images
        TypedArray images = getResources().obtainTypedArray(R.array.categories_images);

        // Setting up each CardView (6 in total)
        for (int i = 0; i < 6; i++) {

            // Finding a reference to the (i + 1) th Category CardView
            int resId = getResources().getIdentifier(getString(R.string.category_card_id_prefix) + (i + 1), "id", getPackageName());
            MaterialCardView cardView = findViewById(resId);

            // Setting OnClickListener to the particular CardView
            cardView.setOnClickListener(this);


            // Finding a reference to the (i + 1) th Category ImageView
            int resIdImg = getResources().getIdentifier(getString(R.string.category_image_id_prefix) + (i + 1), "id", getPackageName());
            ImageView imageView = findViewById(resIdImg);

            // Setting appropriate drawable to the particular ImageView using Glide
            // The drawable resource IDs were stored earlier in the images TypedArray
            Glide
                    .with(this)
                    .load(images.getResourceId(i, 0))
                    .circleCrop()
                    .into(imageView);

            // Finding a reference to the (i + 1) th Category TextView
            int resIdText = getResources().getIdentifier(getString(R.string.category_text_id_prefix) + (i + 1), "id", getPackageName());
            TextView textView = findViewById(resIdText);

            // Setting appropriate category name to the particular TextView
            // The category names were stored earlier in the categories String array
            textView.setText(categories[i]);
        }

        // Recycling the images TypedArray
        images.recycle();
    }


    /**
     * This OnClickListener is attached to each Category CardView
     * @param v is the View that invoked this onClick method
     */
    @Override
    public void onClick(View v) {
        // Declaring a empty String variable
        String query;

        // Assigns value to the String based on the CardView that is clicked
        int id = v.getId();
        if (id == R.id.category_card1) {
            query = "Starter";
        } else if (id == R.id.category_card2) {
            query = "Salad";
        } else if (id == R.id.category_card3) {
            query = "Indian";
        } else if (id == R.id.category_card4) {
            query = "Side";
        } else if (id == R.id.category_card5) {
            query = "Miscellaneous";
        } else if (id == R.id.category_card6) {
            query = "Dessert";
        } else {
            query = null;
        }

        // Launches ExploreActivity with the String variable attached
        if (query != null) {
            Intent intent = new Intent(this, ExploreActivity.class);
            intent.putExtra(RECIPE_CAT, query);
            startActivity(intent);
        }
    }
}