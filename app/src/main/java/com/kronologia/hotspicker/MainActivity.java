package com.kronologia.hotspicker;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kronologia.hotspicker.AppController;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    private String jsonResponse;
    private String baseUrl = "http://www.kronologia.fr/HotsPicker/";

    EditText etHero1;
    EditText etHero2;

    ImageView im1;
    ImageView im2;

    String[] heroNames = {"Xul", "Nova", "Murky", "Artanis"}; //TODO compléter+globaliser

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String heroName = "Kael'Thas";
        String heroVs = "Zeratul";

       etHero1 = (EditText) findViewById(R.id.hero1);
       etHero2 = (EditText) findViewById(R.id.hero2);

        im1 = (ImageView) findViewById(R.id.imageView1);
        im2 = (ImageView) findViewById(R.id.imageView2);

        etHero1.addTextChangedListener(etHeroNameListener);
        etHero2.addTextChangedListener(etHeroNameListener);

       // makeJsonArrayRequest(heroName, heroVs);
    }

    TextWatcher etHeroNameListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "et1 = " + etHero1.getText().toString());
            Log.d(TAG, "et2 = " + etHero2.getText().toString());

            String n1 =  etHero1.getText().toString();
            String n2 =  etHero2.getText().toString();

            //TODO le changement d'image ne marche qu'une fois...

            if(Arrays.asList(heroNames).contains(n1)) {
                int idHero = getResources().getIdentifier(n1, "drawable", getPackageName());
                im1.setImageResource(idHero);
            }

            if(Arrays.asList(heroNames).contains(n2)) {
                int idHero = getResources().getIdentifier(n2, "drawable", getPackageName());
                im1.setImageResource(idHero);
            }

            if(Arrays.asList(heroNames).contains(n1) && Arrays.asList(heroNames).contains(n2)) {
                Log.d(TAG, "Both ET are ok");
                makeJsonArrayRequest(n1, n2);

            }
        }
    };

    private void makeJsonArrayRequest(final String heroName, final String heroVs) {

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    for (int i = 0; i < response.length(); i++) {
                        jsonResponse = "";
                        //Log.d(TAG, "CLASS " + response.get(i).getClass().toString());

                        JSONObject person = (JSONObject) response.get(i);

                        String hero2 = person.getString("hero2");
                        String winrate = person.getString("winrate");
                        String hero1 = person.getString("hero1");

                        jsonResponse += hero1;
                        jsonResponse += " vs " + hero2;
                        jsonResponse += " : " + winrate + "%\n";


                        if((hero1.equals(heroVs) && hero2.equals(heroName)) || (hero2.equals(heroVs) && hero1.equals(heroName))) {
                            Log.d(TAG, jsonResponse);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        JsonArrayRequest req = new JsonArrayRequest(baseUrl + heroName + ".json", respListener, errListener);
        JsonArrayRequest req2 = new JsonArrayRequest(baseUrl + heroVs + ".json", respListener, errListener);

        AppController.getInstance().addToRequestQueue(req);
        AppController.getInstance().addToRequestQueue(req2);
    }
}
