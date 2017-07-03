package com.example.android.pokedex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

public class PokedexDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "PokedexDB";
    private static final int DATABASEVERSION = 3;

    public PokedexDatabaseHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String CREATETABLE = "CREATE TABLE "+
                PokedexContent.PokedexEntry.TABLE_NAME + " (" +
                PokedexContent.PokedexEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PokedexContent.PokedexEntry.NAME_COLUMN + " TEXT," +
                PokedexContent.PokedexEntry.IMAGE_COLUMN + " TEXT" + ");";

        database.execSQL(CREATETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + PokedexContent.PokedexEntry.TABLE_NAME);
        onCreate(database);
    }
}
