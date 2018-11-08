package com.naijaplanet.magosla.android.thebaker.data.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface IngredientsColumn {
    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(INTEGER)
    @References(table = RecipeDatabase.Tables.RECIPES, column = RecipesColumn.ID)
    String RECIPE_ID =
            "recipe_id";

    @DataType(INTEGER)
    @NotNull
    String QUANTITY = "quantity";

    @DataType(TEXT)
    @NotNull
    String MEASURE = "measure";

    @DataType(TEXT)
    @NotNull
    String INGREDIENT = "ingredient";
}
