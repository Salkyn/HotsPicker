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
    private Resources resources;

    public JSONRequests(Resources r, String pack, Activity a) {

        this.resources = r;
        this.iu = new IU(r, pack, a);
        this.TAG = JSONRequests.class.getSimpleName();
        baseUrl = "http://www.kronologia.fr/HotsPicker/";
    };

    public void getBestAgainstTeam(final String[] ennemyTeam, final String[] alliesTeam) {
        Log.i(TAG, "getBestAgainstTeam " + ennemyTeam[0]);
        final Map<String, Double> herosWinrateMap = new HashMap<String, Double>();

        Log.d(TAG, "JsonArrayRequest on " + ennemyTeam.length);

        Response.Listener<JSONArray> respListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

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

                    /*for(String allie : alliesTeam) {
                        if(!allie.equals(resources.getString(R.string.defaultName))) {
                            herosWinrateMap.remove(allie);
                            Log.i(TAG, "Removed " + allie + " from heropool");
                        }
                    }

                    for(String ennemy : ennemyTeam) {
                        if(!ennemy.equals(resources.getString(R.string.defaultName))) {
                            herosWinrateMap.remove(ennemy);
                            Log.i(TAG, "Removed " + ennemy + " from heropool");
                        }
                    }*/

                    for(String removed : MyApplication.getRemovedHeroes()) {
                        herosWinrateMap.remove(removed);
                    }

                    Map orderedHerosWinrateMap = sortByValue(herosWinrateMap);

                    String top1 = HeroesGestion.formatHeroName(orderedHerosWinrateMap.keySet().toArray()[0].toString());
                    String top2 = HeroesGestion.formatHeroName(orderedHerosWinrateMap.keySet().toArray()[1].toString());
                    String top3 = HeroesGestion.formatHeroName(orderedHerosWinrateMap.keySet().toArray()[2].toString());

                    iu.updateIuTop3(top1, top2, top3);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

            }
        };

        for(int i = 0 ; i < ennemyTeam.length ; i++) {
            if(ennemyTeam[i].length() > 0 && !ennemyTeam[i].equals(this.resources.getString(R.string.defaultName))) {
                Log.d(TAG, "request nb " + i);
                JsonArrayRequest req = new JsonArrayRequest(baseUrl + HeroesGestion.formatHeroName(ennemyTeam[i]) + ".json", respListener, errListener);

                AppController.getInstance().addToRequestQueue(req);
            }
        }


    }

    Response.ErrorListener errListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            Log.d(TAG, error.getMessage());
        }
    };

    public Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        Log.i(TAG, sortedMap.keySet().toString());
        Log.i(TAG, sortedMap.values().toString());
        return sortedMap;
    }

}
