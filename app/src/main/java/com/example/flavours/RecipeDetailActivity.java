package com.example.flavours;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String RECIPE_OBJECT = "Recipe Object";
    public static final int REQUEST_CODE = 1;

    private List<LinearLayout> linearLayouts = new ArrayList<>(20);
    private List<EditText> editTexts = new ArrayList<>();
    private List<EditText> ingredientEditTexts = new ArrayList<>();
    private List<EditText> measureEditTexts = new ArrayList<>();

    private NestedScrollView rootView;
    private ImageView recipeImage;
    private EditText recipeTitle;
    private EditText recipeType;
    private EditText recipeArea;
    private EditText recipeInstructions;
    private TextView addIngredientText;
    private RecipeDatabase database;

    private String currentImagePath = null;

    private boolean creatingNewRecipe = false;

    // Should be false by default.
    // Set to true because onPrepareOptionsMenu gets called by the system once.

    private boolean editMode = true;

    private boolean editBtnVisible = false;

    private int ingredientsVisible = 2;

    private Recipe myRecipe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        rootView = findViewById(R.id.root_view);
        recipeImage = findViewById(R.id.recipe_img);
        recipeTitle = findViewById(R.id.recipe_title);
        recipeType = findViewById(R.id.recipe_type);
        recipeArea = findViewById(R.id.recipe_area);
        recipeInstructions = findViewById(R.id.instructions);

        addIngredientText = findViewById(R.id.add_ingredient);

        grabEditTexts();

        for (int i = 1; i <= 20; i++) {
            int resId = getResources().getIdentifier("ingredient" + i, "id", getPackageName());
            LinearLayout layout = findViewById(resId);
            linearLayouts.add(layout);
        }

        database = Room.databaseBuilder(this, RecipeDatabase.class, getString(R.string.db_name)).allowMainThreadQueries().build();

        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_OBJECT)) {
            myRecipe = intent.getParcelableExtra(RECIPE_OBJECT);
        } else {
            creatingNewRecipe = true;
        }

        if (creatingNewRecipe) {
            myRecipe = new Recipe();
            unlockEverything();
        } else {

            if (myRecipe != null) {
                if (!myRecipe.fromApi) {
                    RecipeWithIngredients recipeWithIngredients = database.recipeDao().getRecipeWithIngredients(myRecipe.recipeId);
                    List<Ingredient> ingredients = recipeWithIngredients.ingredients;

                    myRecipe.recipeIngredients = new ArrayList<>();
                    myRecipe.recipeIngredientMeasures = new ArrayList<>();

                    for (int i = 0; i < ingredients.size(); i++) {
                        myRecipe.recipeIngredients.add(ingredients.get(i).ingredient);
                        myRecipe.recipeIngredientMeasures.add(ingredients.get(i).measure);
                    }

                    if (myRecipe.recipeImagePath != null) {
                        Bitmap image = BitmapFactory.decodeFile(myRecipe.recipeImagePath);

                        Glide
                                .with(this)
                                .load(image)
                                .centerCrop()
                                .into(recipeImage);
                    }
                } else {
                    Glide
                            .with(this)
                            .load(myRecipe.getRecipeThumbUrl())
                            .centerCrop()
                            .into(recipeImage);
                }

                ingredientsVisible = myRecipe.recipeIngredients.size();

                recipeImage.setClickable(false);
                recipeImage.setFocusable(false);

                for (int i = 0; i < ingredientsVisible; i++) {
                    linearLayouts.get(i).setVisibility(View.VISIBLE);
                }

                if (myRecipe.getRecipeName() != null) {
                    recipeTitle.setText(myRecipe.getRecipeName());
                }

                if (myRecipe.getRecipeCategory() != null) {
                    recipeType.setText(myRecipe.getRecipeCategory());
                }

                if (myRecipe.getRecipeArea() != null) {
                    recipeArea.setText(myRecipe.getRecipeArea());
                }

                if (myRecipe.getRecipeInstructions() != null) {
                    String instructions = myRecipe.getRecipeInstructions().replace("\n", "\n\n");
                    recipeInstructions.setText(instructions);
                }

                List<String> ingredients = myRecipe.getRecipeIngredients();
                List<String> measures = myRecipe.getRecipeIngredientMeasures();

                if (ingredients != null && measures != null) {
                    for (int i = 0; i < ingredientsVisible; i++) {

                        ingredientEditTexts.get(i).setText(ingredients.get(i));
                        measureEditTexts.get(i).setText(measures.get(i));
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Menu", "OnCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_options_menu, menu);

        MenuItem downloadBtn = menu.findItem(R.id.option_download);
        MenuItem editBtn = menu.findItem(R.id.option_edit);
        MenuItem saveBtn = menu.findItem(R.id.option_save);


        if (creatingNewRecipe) {
            downloadBtn.setVisible(false);
            editBtn.setVisible(false);
        } else if (myRecipe.fromApi) {
            editBtn.setVisible(false);
            saveBtn.setVisible(false);
        } else {
            downloadBtn.setVisible(false);
            editBtnVisible = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_edit:
                invalidateOptionsMenu();
                return true;
            case R.id.option_save:
                saveRecipe();
                return true;
            case R.id.option_download:
                downloadRecipe();
                item.setIcon(R.drawable.ic_download_done);
                item.setTitle(R.string.download_done_option);
                item.setEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("Menu", "onPrepareOptionsMenu");

        MenuItem saveBtn = menu.findItem(R.id.option_save);
        MenuItem editBtn = menu.findItem(R.id.option_edit);

        if (saveBtn.isVisible() == editBtn.isVisible()) {
            if (editMode) {
                lockEverything();
                editBtn.setIcon(R.drawable.ic_edit);
                editBtn.setTitle(R.string.edit_option);
                saveBtn.setEnabled(true);
                saveBtn.setIcon(R.drawable.ic_save);

                editMode = false;
            } else {
                unlockEverything();
                editBtn.setIcon(R.drawable.ic_done);
                editBtn.setTitle(R.string.done_option);
                saveBtn.setEnabled(false);
                saveBtn.setIcon(R.drawable.ic_save_disabled);

                editMode = true;
            }
        } else {
            unlockEverything();
            saveBtn.setEnabled(true);
        }
        return true;
    }

    private void downloadRecipe() {

        URL imageUrl = null;
        Bitmap bitmap;
        File imageFile;
        OutputStream outputStream = null;

        try {
            imageUrl = new URL(myRecipe.recipeThumbUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (imageUrl != null) {
            try {
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

                imageFile = getImageFile();

                try {
                    outputStream = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    myRecipe.recipeImagePath = currentImagePath;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long recipeId = database.recipeDao().insertRecipe(myRecipe);

        List<String> ingredients = myRecipe.getRecipeIngredients();
        List<String> measures = myRecipe.getRecipeIngredientMeasures();

        for (int i = 0; i < myRecipe.recipeIngredients.size(); i++) {
            Ingredient ingredient = new Ingredient(ingredients.get(i), measures.get(i), recipeId);
            database.recipeDao().insertIngredient(ingredient);
        }

        database.recipeDao().setDownloaded(recipeId);

        Snackbar.make(rootView, R.string.download_done_option, Snackbar.LENGTH_LONG).show();
    }

    private void saveRecipe() {
        boolean recipeDownloaded = false;

        if (editBtnVisible) {
            RecipeWithIngredients recipeWithIngredients = database.recipeDao().getRecipeWithIngredients(myRecipe.recipeId);
            Recipe recipe = recipeWithIngredients.recipe;
            List<Ingredient> ingredientsOfRecipe = recipeWithIngredients.ingredients;

            recipeDownloaded = recipe.downloaded;

            database.recipeDao().deleteRecipe(recipe);

            for (Ingredient ingredient : ingredientsOfRecipe) {
                database.recipeDao().deleteIngredient(ingredient);
            }

        }

        // Save modified recipe
        String name = recipeTitle.getText().toString();
        String type = recipeType.getText().toString();
        String area = recipeArea.getText().toString();
        String method = recipeInstructions.getText().toString();

        List<String> ingredients = new ArrayList<>();
        List<String> measures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            EditText ingredientEditText = ingredientEditTexts.get(i);
            EditText measureEditText = measureEditTexts.get(i);

            if (!ingredientEditText.getText().toString().trim().equals("") && !measureEditText.getText().toString().trim().equals("")) {
                ingredients.add(ingredientEditText.getText().toString());
                measures.add(measureEditText.getText().toString());
            }
        }

        Recipe newRecipe = new Recipe(name, type, area, method, null, null, ingredients, measures, false);

        newRecipe.recipeImagePath = currentImagePath;

        long recipeId = database.recipeDao().insertRecipe(newRecipe);

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = new Ingredient(ingredients.get(i), measures.get(i), recipeId);
            database.recipeDao().insertIngredient(ingredient);
        }

        if (recipeDownloaded) {
            database.recipeDao().setDownloaded(recipeId);
        }

        this.setResult(RESULT_OK);
        finish();
    }

    private void lockEverything() {
        recipeImage.setClickable(false);
        recipeImage.setFocusable(false);

        for (EditText editText : editTexts) {
            editText.setFocusable(false);
        }

        addIngredientText.setVisibility(View.GONE);
    }

    private void unlockEverything() {
        recipeImage.setClickable(true);
        recipeImage.setFocusable(true);

        for (EditText editText : editTexts) {
            editText.setFocusableInTouchMode(true);
        }

        addIngredientText.setVisibility(View.VISIBLE);
    }

    private void grabEditTexts() {
        EditText recipeTitle = findViewById(R.id.recipe_title);
        EditText recipeType = findViewById(R.id.recipe_type);
        EditText recipeArea = findViewById(R.id.recipe_area);

        editTexts.add(recipeTitle);
        editTexts.add(recipeType);
        editTexts.add(recipeArea);

        for (int i = 1; i <= 20; i++) {
            int resId1 = getResources().getIdentifier("ing_measure" + i, "id", getPackageName());
            int resId2 = getResources().getIdentifier("ing_name" + i, "id", getPackageName());

            EditText ingredientMeasure = findViewById(resId1);
            EditText ingredientName = findViewById(resId2);

            editTexts.add(ingredientMeasure);
            editTexts.add(ingredientName);

            ingredientEditTexts.add(ingredientName);
            measureEditTexts.add(ingredientMeasure);
        }

        EditText recipeMethod = findViewById(R.id.instructions);
        editTexts.add(recipeMethod);
    }

    // Method to capture image and store in local storage
    public void captureImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.flavours.fileprovider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (currentImagePath != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
                    Glide
                            .with(this)
                            .load(bitmap)
                            .centerCrop()
                            .into(recipeImage);
                }
            }
        }
    }

    public void addIngredient(View view) {
        if (ingredientsVisible < 20) {
            linearLayouts.get(ingredientsVisible).setVisibility(View.VISIBLE);
            ingredientsVisible++;
        }
    }

    /**
     * Author: M. Waqas Pervez
     * <p>
     * Method to reduce the size of a Bitmap
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create a Matrix the multiplication
        Matrix matrix = new Matrix();

        // Resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "img_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
}