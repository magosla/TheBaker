package com.naijaplanet.magosla.android.thebaker.data.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface RecipesColumn {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(TEXT) @NotNull @Unique
    String NAME = "name";

    @DataType(INTEGER) @NotNull
    String SERVINGS = "servings";

    @DataType(TEXT) @NotNull @DefaultValue("''") String IMAGE = "image";

    String STEPS = "steps";
}
