package com.kronologia.hotspicker;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kronologia.hotspicker.AppController;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getSimpleName();

    private String jsonResponse;
    private String baseUrl = "http://www.kronologia.fr/HotsPicker/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String heroName = "Nova";
        String heroVs = "Zeratul";

        makeJsonArrayRequest(heroName, heroVs);
    }

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
