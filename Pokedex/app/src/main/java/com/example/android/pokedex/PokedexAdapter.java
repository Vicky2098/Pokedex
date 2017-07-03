package com.example.android.pokedex;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.pokedex.R.id.IMAGE;
import static com.example.android.pokedex.R.id.imgview;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> {


    Context context;
    Cursor cursor;


    PokedexAdapter(Context context,Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public PokedexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.singleitem,parent,false);
        PokedexViewHolder pokedexViewHolder = new PokedexViewHolder(row);

        return pokedexViewHolder;
    }

    @Override
    public void onBindViewHolder(PokedexViewHolder holder, int position) {

        if(!cursor.moveToPosition(position))
            return;

        String name = cursor.getString(cursor.getColumnIndex(PokedexContent.PokedexEntry.NAME_COLUMN));
        String image = cursor.getString(cursor.getColumnIndex(PokedexContent.PokedexEntry.IMAGE_COLUMN));
        long id = cursor.getLong(cursor.getColumnIndex(PokedexContent.PokedexEntry._ID));

        Picasso.with(context).load(image).into(holder.imageView);
        holder.textView.setText(name.toUpperCase());
        holder.itemView.setTag(id);
        holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }



    class PokedexViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public PokedexViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imgview);
            textView = (TextView) itemView.findViewById(R.id.txtview);
        }

    }
}

