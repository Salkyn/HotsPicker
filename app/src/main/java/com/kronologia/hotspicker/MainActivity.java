package com.kronologia.hotspicker;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kronologia.hotspicker.AppController;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    ImageView imAllies1, imAllies2, imAllies3, imAllies4, imAllies5;
    ImageView imEnemies1, imEnemies2, imEnemies3, imEnemies4, imEnemies5;
    ImageView imChoice1, imChoice2, imChoice3;

    TextView tv;

    LinearLayout imLayout;

    String[] heroNames = {"falstad","gall","greymane","illidan","jaina","kaelthas",
            "kerrigan","lunara","nova","raynor","thebutcher","thrall","tychus",
            "valla","zeratul","abathur","azmodan","gazlowe","lostvikings","murky",
            "nazeebo","sgthammer","sylvanas","zagara","brightwing","kharazim",
            "lili","ltmorales","malfurion","rehgar","tassadar","tyrande","uther",
            "anubarak","artanis","arthas","chen","cho","diablo","etc","johanna",
            "leoric","muradin","rexxar","sonya","stitches","tyrael","liming","xul","dehaka"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Arrays.sort(heroNames); //classement alphabétique des noms pour l'affichage

        setContentView(R.layout.activity_main);

        //Layout qui contient les images de tous les héros
        imLayout = (LinearLayout) findViewById(R.id.images_layout);

        imAllies1 = (ImageView) findViewById(R.id.imageViewAllies1);
        imAllies2 = (ImageView) findViewById(R.id.imageViewAllies2);
        imAllies3 = (ImageView) findViewById(R.id.imageViewAllies3);
        imAllies4 = (ImageView) findViewById(R.id.imageViewAllies4);
        imAllies5 = (ImageView) findViewById(R.id.imageViewAllies5);

        imEnemies1 = (ImageView) findViewById(R.id.imageViewEnemies1);
        imEnemies2 = (ImageView) findViewById(R.id.imageViewEnemies2);
        imEnemies3 = (ImageView) findViewById(R.id.imageViewEnemies3);
        imEnemies4 = (ImageView) findViewById(R.id.imageViewEnemies4);
        imEnemies5 = (ImageView) findViewById(R.id.imageViewEnemies5);

        imChoice1 = (ImageView) findViewById(R.id.imageViewChoice1);
        imChoice2 = (ImageView) findViewById(R.id.imageViewChoice2);
        imChoice3 = (ImageView) findViewById(R.id.imageViewChoice3);

        //Test maj textview, TODO à finir
        tv = (TextView) findViewById(R.id.textViewAllies1);

        //Ajout de toutes les images pour les picks
        //Le nom dans heroNames doit correspondre au nom de la ressource
        for(String hero : heroNames) {
            ImageView i = new ImageView(this);
            int id = getResources().getIdentifier(hero, "drawable", getPackageName());
            i.setImageResource(id);
            i.setOnClickListener(imgClickListener);
            i.setPadding(2,0,2,0); //TODO pas propre
            i.setTag(hero); //pour identifier l'image plus facilement

            imLayout.addView(i);
        }
    }

    //OnClickListener sur la liste complète pour savoir quel héros est choisi
    //TODO ajouter le même listener sur les images "conseil" pour simplifier les picks ?
    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String n1 = v.getTag().toString();

            int idHero = getResources().getIdentifier(n1, "drawable", getPackageName());
            imAllies1.setImageResource(idHero);
            tv.setText(n1);

            makeJsonArrayRequest(n1);
        }
    };

    //Récupère les 3 meilleurs picks contre heroName et maj l'interface
    //TODO factoriser
    private void makeJsonArrayRequest(final String heroName) {

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

                    //Changement des noms avec caractères spéciaux pour que le nom corresponde aux ressources de l'app
                    for(int i = 0 ; i < bestVss.length ; i++) {
                        bestVss[i] = bestVss[i].equals("The Lost Vikings") ? "lostvikings" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("Sgt. Hammer") ? "sgthammer" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("Lt. Morales") ? "ltmorales" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("E.T.C.") ? "etc" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("The Butcher") ? "thebutcher" : bestVss[i];
                        bestVss[i] = bestVss[i].equals("Li-Ming") ? "liming" : bestVss[i];
                    }

                    updateIuTop3(bestVss[0], bestVss[1], bestVss[2]);

                    Log.d(TAG, bestVss[0] + ", " + bestVss[1] + ", " + bestVss[2] + " best Vs " + heroName);

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

    //Maj de l'interface selon les meilleurs héros obtenus
    public void updateIuTop3(String name1, String name2, String name3) {

        int idHero = getResources().getIdentifier(name1.toLowerCase(), "drawable", getPackageName());
        imChoice1.setTag(name1);
        imChoice1.setImageResource(idHero);

        int idHero2 = getResources().getIdentifier(name2.toLowerCase(), "drawable", getPackageName());
        imChoice2.setTag(name2);
        imChoice2.setImageResource(idHero2);

        int idHero3 = getResources().getIdentifier(name3.toLowerCase(), "drawable", getPackageName());
        imChoice3.setTag(name3);
        imChoice3.setImageResource(idHero3);
    }
}
