package com.example.android.pokedex;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import android.os.Handler;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity {

    String NAME,TYPE,IMAGE;
    int HEIGHT,WEIGHT;
    StringBuilder tempString;
    SQLiteDatabase mDatabase;
    public static final int HISTORY_DELETE = 1;
    boolean flag =false;
    String REQUEST_URL;
    EditText nameBox;
    ImageView image;
    TextView name,type,height,weight;
    LinearLayout nLayout,tLayout,wLayout,hLayout;
    Button search;



    Handler messageHandler = new Handler();
    public void displayError(final String errorText) {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
            }
        };
        messageHandler.post(doDisplayError);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nLayout = (LinearLayout) findViewById(R.id.nLayout);
        tLayout = (LinearLayout) findViewById(R.id.tLayout);
        wLayout = (LinearLayout) findViewById(R.id.wLayout);
        hLayout = (LinearLayout) findViewById(R.id.hLayout);
        image = (ImageView) findViewById(R.id.IMAGE);
        search = (Button) findViewById(R.id.searchButton);
        nameBox = (EditText) findViewById(R.id.pokemonName);
        name = (TextView) findViewById(R.id.name);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        type = (TextView) findViewById(R.id.type);

        PokedexDatabaseHelper helper = new PokedexDatabaseHelper(this);
        mDatabase = helper.getWritableDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.historymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
        startActivityForResult(intent,HISTORY_DELETE);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == HISTORY_DELETE && resultCode == RESULT_OK)
        {
            Toast toast = Toast.makeText(this,"Search History Cleared",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }

    public void Asynctask(View view)
    {
        if(nameBox.getText().toString().equals(""))
            Toast.makeText(this,"Enter Pokemon Name",Toast.LENGTH_LONG).show();

        else
        {

            REQUEST_URL = "https://pokeapi.co/api/v2/pokemon/";
            String s = nameBox.getText().toString();
            REQUEST_URL+=s;
            REQUEST_URL+="/";
            nameBox.setText("");
            PokedexTask P = new PokedexTask();
            P.execute();
        }


    }

    public String StreamReader(InputStream inputStream) throws IOException
    {
        StringBuilder builder = new StringBuilder();

        if(inputStream!=null)
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            try {
                String line = reader.readLine();

                while(line!=null)
                {
                    builder.append(line);
                    line=reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return builder.toString();
    }

    public String makeHttpRequest(URL url) throws IOException
    {

        String jsonResponse = "";

        if(url == null)
            return jsonResponse;


        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;


        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200)
            {

                inputStream = urlConnection.getInputStream();
                jsonResponse = StreamReader(inputStream);
            }

            else{
                flag=true;
               displayError("Check what you have entered");}

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();

            if(inputStream!=null)
                inputStream.close();
        }

        return jsonResponse;
    }


    public void extractFromJSON(String JSONPokemon)
    {

        tempString = new StringBuilder();

        try
        {
            JSONObject jsonObj = new JSONObject(JSONPokemon);
            JSONArray jsonArray = jsonObj.getJSONArray("types");
            String temp;
            for(int i=0;i<jsonArray.length();i++)
            {
                temp = jsonArray.getJSONObject(i).getJSONObject("type").getString("name");
                tempString.append(temp);

                if(i!=jsonArray.length()-1)
                    tempString.append(", ");

            }
            NAME = jsonObj.getString("name");
            WEIGHT = jsonObj.getInt("weight");
            HEIGHT = jsonObj.getInt("height");
            IMAGE = jsonObj.getJSONObject("sprites").getString("front_default");
            TYPE = tempString.toString();

            AddToDatabase();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AddToDatabase()
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PokedexContent.PokedexEntry.NAME_COLUMN,NAME);
        contentValues.put(PokedexContent.PokedexEntry.IMAGE_COLUMN,IMAGE);
        mDatabase.insert(PokedexContent.PokedexEntry.TABLE_NAME,null,contentValues);
    }

    public void screenUpdate()
    {
        tLayout.setVisibility(View.VISIBLE);
        nLayout.setVisibility(View.VISIBLE);
        hLayout.setVisibility(View.VISIBLE);
        wLayout.setVisibility(View.VISIBLE);
        Picasso.with(this).load(IMAGE).into(image);
        type.setText(TYPE.toUpperCase());
        weight.setText(String.valueOf(WEIGHT));
        height.setText(String.valueOf(HEIGHT));
        name.setText(NAME.toUpperCase());
    }


    public class PokedexTask extends AsyncTask<URL,Void,Void>{

        @Override
        protected Void doInBackground(URL... urls) {


            URL url = null;
            String jsonResponse = "";
            try {
                url = new URL(REQUEST_URL);
            } catch (MalformedURLException e) {
            }

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            extractFromJSON(jsonResponse);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if(flag==false)
            screenUpdate();}
    }
}