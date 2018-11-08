package com.naijaplanet.magosla.android.thebaker.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import static com.naijaplanet.magosla.android.thebaker.data.provider.RecipeDatabase.Tables.RECIPES;

@SuppressWarnings({"ALL", "WeakerAccess"})
@Database(version = RecipeDatabase.VERSION ,
        packageName = "com.naijaplanet.magosla.android.thebaker.provider")
public class RecipeDatabase {

    public static final int VERSION = 1;

    @ExecOnCreate
    public static final String EXEC_ON_CREATE = "SELECT * FROM " + RECIPES;

    private RecipeDatabase() {
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

    public static class Tables {

        @Table(RecipesColumn.class)
        @IfNotExists
        public static final String RECIPES = "recipes";

        @Table(IngredientsColumn.class)
        @IfNotExists
        public static final String INGREDIENTS = "ingredients";

        @Table(RecipeStepsColumn.class)
        @IfNotExists
        public static final String RECIPE_STEPS = "recipe_steps";
    }
}

