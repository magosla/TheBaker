package com.naijaplanet.magosla.android.thebaker.data.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


@SuppressWarnings("ALL")
public class Recipe implements Parcelable {
    // This is to de-serialize the object
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    private int id;
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<RecipeStep> steps;

    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        in.readTypedList(ingredients, Ingredient.CREATOR);
        in.readTypedList(steps, RecipeStep.CREATOR);
    }

    /**
     * To number of steps available
     * @return the number of steps
     */
    public int getStepsCount(){
        return steps.size();
    }

    @SuppressWarnings("unused")
    public RecipeStep getStepAt(int index){
        if(index < getStepsCount()) {
            return steps.get(index);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(servings);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
    }
}
