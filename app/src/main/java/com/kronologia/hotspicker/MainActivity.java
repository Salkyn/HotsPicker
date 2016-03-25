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

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    private String jsonResponse;
    private String baseUrl = "http://www.kronologia.fr/HotsPicker/";

    ImageView im1;
    ImageView im2;

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
        setContentView(R.layout.activity_main);

        imLayout = (LinearLayout) findViewById(R.id.images_layout);

        im1 = (ImageView) findViewById(R.id.imageView1);
        im2 = (ImageView) findViewById(R.id.imageView2);

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

                    String bestVs="";
                    double bestWinrate = 100;

                    for (int i = 0; i < response.length(); i++) {
                        jsonResponse = "";

                        JSONObject person = (JSONObject) response.get(i);

                        double winrate = Double.valueOf(person.getString("winrate"));

                        //Log.d(TAG, person.getString("hero2") + " " + winrate);

                        if(winrate < bestWinrate) {
                            bestWinrate = winrate;
                            bestVs =  person.getString("hero2");
                        }

                    }

                    bestVs = bestVs.equals("The Lost Vikings") ? "lostvikings" : bestVs;
                    bestVs = bestVs.equals("Sgt. Hammer") ? "sgthammer" : bestVs;
                    bestVs = bestVs.equals("Ly. Morales") ? "ltmorales" : bestVs;
                    bestVs = bestVs.equals("E.T.C.") ? "etc" : bestVs;
                    bestVs = bestVs.equals("The Butcher") ? "thebutcher" : bestVs;

                    int idHero = getResources().getIdentifier(bestVs.toLowerCase(), "drawable", getPackageName());
                    im2.setImageResource(idHero);

                    Log.d(TAG, bestVs + " is the best against " + heroName + " (" + bestWinrate + "%)");
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
