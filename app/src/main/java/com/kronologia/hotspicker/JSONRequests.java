package com.kronologia.hotspicker;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Maxence on 06/04/2016.
 */
public class JSONRequests {

    private IU iu;
    private String TAG;
    private String baseUrl;

    public JSONRequests(Resources r, String pack, Activity a) {
        this.iu = new IU(r, pack, a);

        this.TAG = JSONRequests.class.getSimpleName();
        baseUrl = "http://www.kronologia.fr/HotsPicker/";
    };

    //Récupère les 3 meilleurs picks contre heroName et maj l'interface
    //TODO factoriser
    public void makeJsonArrayRequest(final String heroName) {

        Log.d(TAG, "JsonArrayRequest with " + heroName);

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    String[] bestVss = {"","",""};
                    double bestWinrate = 100;

                    //On parcours les résultats héros par héros pour avoir le meilleur winrate vs heroName
                    //TODO pt-ê faut-il parcourir la boucle 3 fois pour que ce soit juste ?
                    for (int i = 0; i < response.length(); i++) {
                        String jsonResponse = "";

                        JSONObject person = (JSONObject) response.get(i);

                        double winrate = Double.valueOf(person.getString("winrate"));


                        if(winrate < bestWinrate) {
                            bestWinrate = winrate;
                            bestVss[2] = bestVss[1];
                            bestVss[1] = bestVss[0];
                            bestVss[0] =  person.getString("hero2");
                        }

                    }

                    //Changement des noms avec caractères spéciaux pour que le nom corresponde aux ressources de l'app
                    for(int i = 0 ; i < bestVss.length ; i++) {
                        bestVss[i] = HeroesGestion.formatHeroName(bestVss[i]);
                    }

                    iu.updateIuTop3(bestVss[0], bestVss[1], bestVss[2]);

                    Log.d(TAG, bestVss[0] + ", " + bestVss[1] + ", " + bestVss[2] + " best Vs " + heroName);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

            }
        };

        JsonArrayRequest req = new JsonArrayRequest(baseUrl + heroName + ".json", respListener, errListener);

        AppController.getInstance().addToRequestQueue(req);
    }

    public void getBestAgainstTeam(final String[] enemyTeam) {
        final Map<String, Double> herosWinrateMap = new HashMap<String, Double>();
        final String name = enemyTeam[0];

        Log.d(TAG, "JsonArrayRequest on " + enemyTeam.length);

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                String[] bestVss = {"","",""};
                double bestWinrate = 500;

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);
                        String heroName = HeroesGestion.formatHeroName(person.getString("hero2"));

                        if(herosWinrateMap.get(heroName) == null) {
                            herosWinrateMap.put(heroName, Double.valueOf(person.getString("winrate")));
                        } else {
                            Double lastValue = herosWinrateMap.get(heroName);
                            herosWinrateMap.put(heroName, lastValue + Double.valueOf(person.getString("winrate")));
                        }
                    }

                    Map orderedHerosWinrateMap = sortByValue(herosWinrateMap);

                    String top1 = orderedHerosWinrateMap.keySet().toArray()[0].toString();
                    String top2 = orderedHerosWinrateMap.keySet().toArray()[1].toString();
                    String top3 = orderedHerosWinrateMap.keySet().toArray()[2].toString();

                    iu.updateIuTop3(top1, top2, top3);
                    Log.i(TAG, "Top : (" + top1 + ", " + top2 + ", " + top3 + ")");
                    Log.i(TAG, "Winrates : " + orderedHerosWinrateMap.get(top1) + " ; " + orderedHerosWinrateMap.get(top2) + " ; " + orderedHerosWinrateMap.get(top3));



                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

            }
        };

        for(int i = 0 ; i < enemyTeam.length ; i++) {
            Log.d(TAG, "request nb " + i);
            JsonArrayRequest req = new JsonArrayRequest(baseUrl + enemyTeam[i] + ".json", respListener, errListener);

            AppController.getInstance().addToRequestQueue(req);
        }


    }

    Response.ErrorListener errListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            Log.d(TAG, error.getMessage());
        }
    };

    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
}