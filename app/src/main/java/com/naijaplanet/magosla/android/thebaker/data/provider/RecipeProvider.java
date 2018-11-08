package com.naijaplanet.magosla.android.thebaker.data.provider;

import android.net.Uri;

import com.naijaplanet.magosla.android.thebaker.BuildConfig;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

@ContentProvider(
        authority = RecipeProvider.AUTHORITY,
        database = RecipeDatabase.class,
        packageName = "com.naijaplanet.magosla.android.thebaker.provider"
)
public final class RecipeProvider {
    private RecipeProvider(){}

    public static final String AUTHORITY = BuildConfig.PROVIDER_AUTHORITY;

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String INGREDIENTS = "ingredients";
        String RECIPES = "recipes";
        String RECIPE_STEPS = "recipe_steps";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = RecipeDatabase.Tables.RECIPES)
    public static class Recipes{
        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();

            map.put(RecipesColumn.STEPS, STEPS_COUNT);

            return map;
        }

        @ContentUri(
                path = Path.RECIPES,
                type = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipesColumn.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPES);

        @SuppressWarnings("unused")
        @InexactContentUri(
                path = Path.RECIPES + "/#",
                name = "RECIPE_ID",
                type = "vnd.android.cursor.item/recipe",
                whereColumn = RecipesColumn.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPES, String.valueOf(id));
        }

        static final String STEPS_COUNT = "(SELECT COUNT(*) FROM "
                + RecipeDatabase.Tables.RECIPE_STEPS
                + " WHERE "
                + RecipeDatabase.Tables.RECIPE_STEPS
                + "."
                + RecipeStepsColumn.RECIPE_ID
                + "="
                + RecipeDatabase.Tables.RECIPES
                + "."
                + RecipesColumn.ID
                + ")";
    }

    @SuppressWarnings("unused")
    @TableEndpoint(table = RecipeDatabase.Tables.INGREDIENTS)
    public static class Ingredients{
        @ContentUri(
                path = Path.INGREDIENTS,
                type = "vnd.android.cursor.dir/ingredient")
        public static final Uri CONTENT_URI = buildUri(Path.INGREDIENTS);

        @InexactContentUri(
                name = "INGREDIENT_ID",
                path = Path.INGREDIENTS + "/#",
                type = "vnd.android.cursor.item/ingredient",
                whereColumn = IngredientsColumn.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.INGREDIENTS, String.valueOf(id));
        }

    }

    @TableEndpoint(table = RecipeDatabase.Tables.RECIPE_STEPS)
    public static class RecipeSteps{
        @ContentUri(
                path = Path.RECIPE_STEPS,
                type = "vnd.android.cursor.dir/recipe_step")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPE_STEPS);
        @SuppressWarnings("unused")
        @InexactContentUri(
                name = "RECIPE_STEP_ID",
                path = Path.RECIPE_STEPS + "/#",
                type = "vnd.android.cursor.item/recipe_step",
                whereColumn = RecipeStepsColumn.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPE_STEPS, String.valueOf(id));
        }

    }
}
