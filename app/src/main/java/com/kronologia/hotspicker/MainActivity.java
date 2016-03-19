package com.kronologia.hotspicker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeJsonArrayRequest();
    }

    private void makeJsonArrayRequest() {

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    jsonResponse = "";
                    for (int i = 0; i < response.length(); i++) {

                        //Log.d(TAG, response.get(i).toString());

                        Log.d(TAG, "CLASS " + response.get(i).getClass().toString());

                        JSONObject person = (JSONObject) response.get(i);

                        String hero2 = person.getString("hero2");
                        String winrate = person.getString("winrate");
                        String hero1 = person.getString("hero1");

                        jsonResponse += hero1;
                        jsonResponse += " vs " + hero2;
                        jsonResponse += " : " + winrate;

                    }

                    Log.d(TAG, jsonResponse);

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

        JsonArrayRequest req = new JsonArrayRequest("http://www.kronologia.fr/HotsPicker/Azmodan.json", respListener, errListener);

        // Adding request to request queue
       AppController.getInstance().addToRequestQueue(req);
    }
}
