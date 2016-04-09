package com.kronologia.hotspicker;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kronologia.hotspicker.AppController;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    public JSONRequests jsonRequests;

    private static String TAG = MainActivity.class.getSimpleName();

    ImageView imAllies1, imAllies2, imAllies3, imAllies4, imAllies5;
    ImageView imEnemies1, imEnemies2, imEnemies3, imEnemies4, imEnemies5;
    TextView tvAllies1, tvAllies2, tvAllies3, tvAllies4, tvAllies5;
    TextView tvEnemies1, tvEnemies2, tvEnemies3, tvEnemies4, tvEnemies5;
    ImageView imChoice1, imChoice2, imChoice3;

    LinearLayout imLayout;

    String[] heroNames = {"falstad","gall","greymane","illidan","jaina","kaelthas",
            "kerrigan","lunara","nova","raynor","thebutcher","thrall","tychus",
            "valla","zeratul","abathur","azmodan","gazlowe","lostvikings","murky",
            "nazeebo","sgthammer","sylvanas","zagara","brightwing","kharazim",
            "lili","ltmorales","malfurion","rehgar","tassadar","tyrande","uther",
            "anubarak","artanis","arthas","chen","cho","diablo","etc","johanna",
            "leoric","muradin","rexxar","sonya","stitches","tyrael","liming","xul","dehaka"};

    int draftPickOrder = 0;

    ImageView[] imPickOrder;
    TextView[] tvPickOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jsonRequests = new JSONRequests(getResources(), getPackageName(), this);

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

        tvAllies1 = (TextView) findViewById(R.id.textViewAllies1);
        tvAllies2 = (TextView) findViewById(R.id.textViewAllies2);
        tvAllies3 = (TextView) findViewById(R.id.textViewAllies3);
        tvAllies4 = (TextView) findViewById(R.id.textViewAllies4);
        tvAllies5 = (TextView) findViewById(R.id.textViewAllies5);

        tvEnemies1 = (TextView) findViewById(R.id.textViewEnemies1);
        tvEnemies2 = (TextView) findViewById(R.id.textViewEnemies2);
        tvEnemies3 = (TextView) findViewById(R.id.textViewEnemies3);
        tvEnemies4 = (TextView) findViewById(R.id.textViewEnemies4);
        tvEnemies5 = (TextView) findViewById(R.id.textViewEnemies5);

        imChoice1 = (ImageView) findViewById(R.id.imageViewChoice1);
        imChoice2 = (ImageView) findViewById(R.id.imageViewChoice2);
        imChoice3 = (ImageView) findViewById(R.id.imageViewChoice3);

        imPickOrder = setTeamOrder(1); //La team "Enemies" est la première à pick
        tvPickOrder = setTvTeamOrder(1);

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

            if(draftPickOrder < 10) {

                String n1 = v.getTag().toString();

                int idHero = getResources().getIdentifier(n1, "drawable", getPackageName());
                imPickOrder[draftPickOrder].setImageResource(idHero);
                tvPickOrder[draftPickOrder].setText(n1);

                jsonRequests.makeJsonArrayRequest(n1);

                draftPickOrder++;

                //On cache l'image du héros sélectionner pour pas pouvoir le sélectionner plusieurs fois
                View currView = findViewById(android.R.id.content);
                View im = currView.findViewWithTag(n1);
                im.setVisibility(View.GONE);

            } else {
                String[] myTeam = {tvAllies1.getText().toString(), tvAllies2.getText().toString(), tvAllies3.getText().toString(), tvAllies4.getText().toString(), tvAllies5.getText().toString()};
                String[] ennemyTeam = {tvEnemies1.getText().toString(), tvEnemies2.getText().toString(), tvEnemies3.getText().toString(), tvEnemies4.getText().toString(), tvEnemies5.getText().toString()};
                jsonRequests.getBestAgainstTeam(ennemyTeam);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(draftPickOrder == 0) {
                Log.w(TAG, "Can't cancel last pick");
                return true;
            }
            draftPickOrder--;

            String n1 = tvPickOrder[draftPickOrder].getText().toString();

            int idDefault = getResources().getIdentifier("default1", "drawable", getPackageName());
            imPickOrder[draftPickOrder].setImageResource(idDefault);
            tvPickOrder[draftPickOrder].setText("...");

            //On reaffiche l'image du héros sélectionner pour pouvoir le sélectionner à nouveau
            View currView = findViewById(android.R.id.content);
            View im = currView.findViewWithTag(n1);
            im.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public ImageView[] setTeamOrder(int alliesTeamOrder) {

        ImageView result[];

        if(alliesTeamOrder==1) {
            result = new ImageView[] {imAllies1,imEnemies1,imEnemies2,imAllies2,imAllies3,imEnemies3,imEnemies4,imAllies4,imAllies5,imEnemies5};
        } else {
            result = new ImageView[] {imEnemies1,imAllies1,imAllies2,imEnemies2,imEnemies3,imAllies3,imAllies4,imEnemies4,imEnemies5,imAllies5};
        }

        return result;
    }

    public TextView[] setTvTeamOrder(int alliesTeamOrder) {

        TextView result[];

        if(alliesTeamOrder==1) {
            result = new TextView[] {tvAllies1,tvEnemies1,tvEnemies2,tvAllies2,tvAllies3,tvEnemies3,tvEnemies4,tvAllies4,tvAllies5,tvEnemies5};
        } else {
            result = new TextView[] {tvEnemies1,tvAllies1,tvAllies2,tvEnemies2,tvEnemies3,tvAllies3,tvAllies4,tvEnemies4,tvEnemies5,tvAllies5};
        }

        return result;
    }
}
