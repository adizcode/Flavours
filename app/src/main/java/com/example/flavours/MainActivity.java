package com.example.flavours;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

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

    // ArrayLists used to hold references to the views defined in XML
    private List<MaterialCardView> cardViews = new ArrayList<>(6);
    private List<ImageView> imageViews = new ArrayList<>(6);
    private List<TextView> textViews = new ArrayList<>(6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Finding a reference to the toolbar and setting it as the action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Finding a reference to the bottom navigation bar and setting it up
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Declaring empty intent variable
                Intent intent;

                // Creating transition animation for shared toolbar and bottom navigation bar
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create((View) toolbar, "toolbar"), Pair.create((View) bottomNavigationView, "navigation"));

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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);

                        break;

                    // MyRecipesActivity Tab
                    case R.id.my_recipes:

                        // Launches MyRecipesActivity with the transition
                        intent = new Intent(MainActivity.this, MyRecipesActivity.class);
                        startActivity(intent, options.toBundle());

                        // Finishes current Activity after 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);

                        break;

                    // Empty default case
                    default:
                }

                // Should return true to display the clicked item as the selected item
                // In this app, each tab is linked to a separate Activity, albeit with identical bottom navigation bars
                // Hence returning false
                return false;
            }
        });

        // Retrieving category names from resources
        String[] categories = getResources().getStringArray(R.array.categories);

        // Retrieving drawable resource IDs for images
        // TODO: Understand why TypedArrays are used
        TypedArray images = getResources().obtainTypedArray(R.array.categories_images);

        // Setting up each CardView (6 in total)
        for (int i = 0; i < 6; i++) {

            // Finding a reference to the (i + 1) th Category CardView
            int resId = getResources().getIdentifier(getString(R.string.category_card_id_prefix) + (i + 1), "id", getPackageName());
            MaterialCardView cardView = findViewById(resId);

            // Setting OnClickListener to the particular CardView
            cardView.setOnClickListener(this);

            // Adding the CardView to the cardViews ArrayList (for future reference)
            cardViews.add(cardView);


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

            // Adding the ImageView to the imageViews ArrayList (for future reference)
            imageViews.add(imageView);

            // Finding a reference to the (i + 1) th Category TextView
            int resIdText = getResources().getIdentifier(getString(R.string.category_text_id_prefix) + (i + 1), "id", getPackageName());
            TextView textView = findViewById(resIdText);

            // Setting appropriate category name to the particular TextView
            // The category names were stored earlier in the categories String array
            textView.setText(categories[i]);

            // Adding the TextView to the textViews ArrayList (for future reference)
            textViews.add(textView);
        }

        // Recycling the images TypedArray
        // TODO: Understand this line
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
        switch (v.getId()) {
            case R.id.category_card1:
                query = "Starter";
                break;
            case R.id.category_card2:
                query = "Salad";
                break;
            case R.id.category_card3:
                query = "Indian";
                break;
            case R.id.category_card4:
                query = "Side";
                break;
            case R.id.category_card5:
                query = "Miscellaneous";
                break;
            case R.id.category_card6:
                query = "Dessert";
                break;
            default:
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