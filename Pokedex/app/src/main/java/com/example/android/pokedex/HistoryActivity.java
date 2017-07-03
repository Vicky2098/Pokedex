package com.example.android.pokedex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class HistoryActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    SQLiteDatabase database;
    PokedexAdapter adapter;
    PokedexDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = new PokedexDatabaseHelper(this);
        database  = dbHelper.getWritableDatabase();

        Cursor cursor = getAllItems();
        adapter = new PokedexAdapter(this,cursor);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();
                deleteItem(id);

            }
        }).attachToRecyclerView(recyclerView);

    }

    public void deleteItem(long id)
    {
        database.delete(PokedexContent.PokedexEntry.TABLE_NAME, PokedexContent.PokedexEntry._ID + "=" + id,null);
        adapter.notifyDataSetChanged();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Cursor cursor = getAllItems();
        //adapter = new PokedexAdapter(this,cursor);
        recyclerView.setAdapter(adapter);

    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clearhistory,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(adapter.getItemCount() > 0)
        {
            database.delete(PokedexContent.PokedexEntry.TABLE_NAME,null,null);
            adapter.notifyDataSetChanged();

        }

        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();

        return true;
    }


    public Cursor getAllItems()
    {
        return database.query(
                PokedexContent.PokedexEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }

}
