package com.kronologia.hotspicker;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kronologia.hotspicker.AppController;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    private String jsonResponse;
    private String baseUrl = "http://www.kronologia.fr/HotsPicker/";

    ImageView im1, im2, im3, im4;
    LinearLayout imLayout;

    String[] heroNames = {"falstad","gall","greymane","illidan","jaina","kaelthas",
            "kerrigan","liming","lunara","nova","raynor","thebutcher","thrall",
            "tychus","valla","zeratul","abathur","azmodan","gazlowe","lostvikings",
            "murky","nazeebo","sgthammer","sylvanas","xul","zagara","brightwing",
            "kharazim","lili","ltmorales","malfurion","rehgar","tassadar","tyrande",
            "uther","anubarak","artanis","arthas","chen","cho","diablo","etc","johanna",
            "leoric","muradin","rexxar","sonya","stitches","tyrael"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Arrays.sort(heroNames);

        setContentView(R.layout.activity_main);

        imLayout = (LinearLayout) findViewById(R.id.images_layout);

        im1 = (ImageView) findViewById(R.id.imageView1);
        im2 = (ImageView) findViewById(R.id.imageView2);
        im3 = (ImageView) findViewById(R.id.imageView3);
        im4 = (ImageView) findViewById(R.id.imageView4);

        for(String hero : heroNames) {
            ImageView i = new ImageView(this);
            int id = getResources().getIdentifier(hero, "drawable", getPackageName());
            i.setImageResource(id);
            i.setOnClickListener(imgClickListener);
            i.setTag(hero);

            imLayout.addView(i);
        }
    }

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String n1 = v.getTag().toString();

            int idHero = getResources().getIdentifier(n1, "drawable", getPackageName());
            im1.setImageResource(idHero);

            makeJsonArrayRequest(n1);
        }
    };


    private void makeJsonArrayRequest(final String heroName) {

        Log.d(TAG, "JsonArrayRequest with " + heroName);

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    String[] bestVss= new String[3];
                    double bestWinrate = 100;

                    for (int i = 0; i < response.length(); i++) {
                        jsonResponse = "";

                        JSONObject person = (JSONObject) response.get(i);

                        double winrate = Double.valueOf(person.getString("winrate"));


                        if(winrate < bestWinrate) {
                            bestWinrate = winrate;
                            bestVss[2] = bestVss[1];
                            bestVss[1] = bestVss[0];
                            bestVss[0] =  person.getString("hero2");
                        }

                    }

                    for(int i = 0 ; i < bestVss.length ; i++) {
                        bestVss[i] = bestVss[i].equals("The Lost Vikings") ? "lostvikings" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("Sgt. Hammer") ? "sgthammer" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("Ly. Morales") ? "ltmorales" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("E.T.C.") ? "etc" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("The Butcher") ? "thebutcher" : bestVss[i];
                    }

                    int idHero = getResources().getIdentifier(bestVss[0].toLowerCase(), "drawable", getPackageName());
                    im2.setImageResource(idHero);

                    int idHero2 = getResources().getIdentifier(bestVss[1].toLowerCase(), "drawable", getPackageName());
                    im3.setImageResource(idHero2);

                    int idHero3 = getResources().getIdentifier(bestVss[2].toLowerCase(), "drawable", getPackageName());
                    im4.setImageResource(idHero3);

                    Log.d(TAG, bestVss[0] + "," + bestVss[1] + "," + bestVss[2] + " best Vs " + heroName);
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

        AppController.getInstance().addToRequestQueue(req);
    }
}
