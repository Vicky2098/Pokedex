package com.example.android.pokedex;

import android.provider.BaseColumns;

public class PokedexContent{

    public class PokedexEntry implements BaseColumns{

        public static final String NAME_COLUMN = "NAME";
        public static final String IMAGE_COLUMN = "IMAGE";
        public static final String TABLE_NAME = "POKEMON";

    }
}
