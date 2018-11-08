package com.naijaplanet.magosla.android.thebaker.data.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface RecipeStepsColumn {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(INTEGER) @References(table = RecipeDatabase.Tables.RECIPES, column = RecipesColumn.ID) String RECIPE_ID =
            "recipe_id";

    @DataType(INTEGER) @NotNull String STEP_NO =
            "step_no";


    @DataType(TEXT) @NotNull @DefaultValue("''")
    String SHORT_DESCRIPTION = "short_description";

    @DataType(TEXT) @NotNull @DefaultValue("''")
    String DESCRIPTION = "description";

    @DataType(TEXT) @NotNull @DefaultValue("''")
    String VIDEO_URL = "video_url";

    @DataType(TEXT) @NotNull @DefaultValue("''")
    String THUMBNAIL = "thumbnail";
}
