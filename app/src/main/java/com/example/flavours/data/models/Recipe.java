package com.example.flavours.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    public long recipeId;

    @ColumnInfo
    public String recipeName;

    @ColumnInfo
    public String recipeCategory;

    @ColumnInfo
    public String recipeArea;

    @ColumnInfo
    public String recipeInstructions;

    @ColumnInfo
    public String recipeImagePath;

    @ColumnInfo
    public boolean downloaded = false;

    @Ignore
    public boolean fromApi = false;

    @Ignore
    public List<String> recipeIngredients;

    @Ignore
    public List<String> recipeIngredientMeasures;

    @Ignore
    public String recipeThumbUrl;

    @Ignore
    public String recipeYoutubeUrl;

    public Recipe() {}

    public Recipe(String recipeName,
                  String recipeCategory,
                  String recipeArea,
                  String recipeInstructions,
                  String recipeThumbUrl,
                  String recipeYoutubeUrl,
                  List<String> recipeIngredients,
                  List<String> recipeIngredientMeasures,
                  boolean fromApi) {

        this.recipeName = recipeName;
        this.recipeCategory = recipeCategory;
        this.recipeArea = recipeArea;
        this.recipeInstructions = recipeInstructions;
        this.recipeThumbUrl = recipeThumbUrl;
        this.recipeYoutubeUrl = recipeYoutubeUrl;
        this.recipeIngredients = recipeIngredients;
        this.recipeIngredientMeasures = recipeIngredientMeasures;
        this.fromApi = fromApi;
    }

    protected Recipe(Parcel in) {
        recipeId = in.readLong();
        recipeName = in.readString();
        recipeCategory = in.readString();
        recipeArea = in.readString();
        recipeInstructions = in.readString();
        recipeImagePath = in.readString();
        downloaded = in.readByte() != 0;
        fromApi = in.readByte() != 0;
        recipeIngredients = in.createStringArrayList();
        recipeIngredientMeasures = in.createStringArrayList();
        recipeThumbUrl = in.readString();
        recipeYoutubeUrl = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public String getRecipeArea() {
        return recipeArea;
    }

    public String getRecipeInstructions() {
        return recipeInstructions;
    }

    public String getRecipeThumbUrl() {
        return recipeThumbUrl;
    }

    // TODO: Remove if unnecessary
    public String getRecipeYoutubeUrl() {
        return recipeYoutubeUrl;
    }

    public List<String> getRecipeIngredients() {
        return recipeIngredients;
    }

    public List<String> getRecipeIngredientMeasures() {
        return recipeIngredientMeasures;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(recipeId);
        dest.writeString(recipeName);
        dest.writeString(recipeCategory);
        dest.writeString(recipeArea);
        dest.writeString(recipeInstructions);
        dest.writeString(recipeImagePath);
        dest.writeByte((byte) (downloaded ? 1 : 0));
        dest.writeByte((byte) (fromApi ? 1 : 0));
        dest.writeStringList(recipeIngredients);
        dest.writeStringList(recipeIngredientMeasures);
        dest.writeString(recipeThumbUrl);
        dest.writeString(recipeYoutubeUrl);
    }
}