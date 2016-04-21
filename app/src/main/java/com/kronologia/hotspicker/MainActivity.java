package com.kronologia.hotspicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends Activity {

    public JSONRequests jsonRequests;
    public IU iu;

    private static String TAG = MainActivity.class.getSimpleName();

    ImageView imAllies1, imAllies2, imAllies3, imAllies4, imAllies5;
    ImageView imEnnemies1, imEnnemies2, imEnnemies3, imEnnemies4, imEnnemies5;
    ImageView imChoice1, imChoice2, imChoice3;
    TextView tvAllies1, tvAllies2, tvAllies3, tvAllies4, tvAllies5;
    TextView tvennemies1, tvennemies2, tvennemies3, tvennemies4, tvennemies5;

    Button resetButton, switchTeamsButton;

    Drawable topickAllies_drawable, topickEnnemies_drawable;

    LinearLayout imLayout;

    String[] heroNames = {"falstad","gall","greymane","illidan","jaina","kaelthas",
            "kerrigan","lunara","nova","raynor","thebutcher","thrall","tychus",
            "valla","zeratul","abathur","azmodan","gazlowe","lostvikings","murky",
            "nazeebo","sgthammer","sylvanas","zagara","brightwing","kharazim",
            "lili","ltmorales","malfurion","rehgar","tassadar","tyrande","uther",
            "anubarak","artanis","arthas","chen","cho","diablo","etc","johanna",
            "leoric","muradin","rexxar","sonya","stitches","tyrael","liming","xul","dehaka",
            "tracer"};

    int draftPickOrder = 0;

    ImageView[] imPickOrder;
    TextView[] tvPickOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jsonRequests = new JSONRequests(getResources(), getPackageName(), this);
        iu = new IU(getResources(), getPackageName(), this);

        Arrays.sort(heroNames); //classement alphabétique des noms pour l'affichage

        setContentView(R.layout.activity_main);

        //Layout qui contient les images de tous les héros
        imLayout = (LinearLayout) findViewById(R.id.images_layout);

        imAllies1 = (ImageView) findViewById(R.id.imageViewAllies1);
        imAllies2 = (ImageView) findViewById(R.id.imageViewAllies2);
        imAllies3 = (ImageView) findViewById(R.id.imageViewAllies3);
        imAllies4 = (ImageView) findViewById(R.id.imageViewAllies4);
        imAllies5 = (ImageView) findViewById(R.id.imageViewAllies5);

        imEnnemies1 = (ImageView) findViewById(R.id.imageViewEnnemies1);
        imEnnemies2 = (ImageView) findViewById(R.id.imageViewEnnemies2);
        imEnnemies3 = (ImageView) findViewById(R.id.imageViewEnnemies3);
        imEnnemies4 = (ImageView) findViewById(R.id.imageViewEnnemies4);
        imEnnemies5 = (ImageView) findViewById(R.id.imageViewEnnemies5);

        imChoice1 = (ImageView) findViewById(R.id.imageViewChoice1);
        imChoice2 = (ImageView) findViewById(R.id.imageViewChoice2);
        imChoice3 = (ImageView) findViewById(R.id.imageViewChoice3);

        tvAllies1 = (TextView) findViewById(R.id.textViewAllies1);
        tvAllies2 = (TextView) findViewById(R.id.textViewAllies2);
        tvAllies3 = (TextView) findViewById(R.id.textViewAllies3);
        tvAllies4 = (TextView) findViewById(R.id.textViewAllies4);
        tvAllies5 = (TextView) findViewById(R.id.textViewAllies5);

        tvennemies1 = (TextView) findViewById(R.id.textViewEnnemies1);
        tvennemies2 = (TextView) findViewById(R.id.textViewEnnemies2);
        tvennemies3 = (TextView) findViewById(R.id.textViewEnnemies3);
        tvennemies4 = (TextView) findViewById(R.id.textViewEnnemies4);
        tvennemies5 = (TextView) findViewById(R.id.textViewEnnemies5);

        resetButton = (Button) findViewById(R.id.newDraftButton);
        resetButton.setOnClickListener(newDraftListener);

        switchTeamsButton = (Button) findViewById(R.id.switchTeamsButton);
        switchTeamsButton.setOnClickListener(switchTeamsListener);

        int idDrawable_topickAllies = getResources().getIdentifier("default1_c", "drawable", getPackageName());
        topickAllies_drawable = getResources().getDrawable(idDrawable_topickAllies);

        int idDrawable_topickEnnemies = getResources().getIdentifier("default2_c", "drawable", getPackageName());
        topickEnnemies_drawable = getResources().getDrawable(idDrawable_topickEnnemies);

        setTeamOrder(1); //La team "Allies" est la première à pick par défaut
        setTvTeamOrder(1);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Ajout de toutes les images pour les picks
        //Le nom dans heroNames doit correspondre au nom de la ressource
        for(String hero : heroNames) {
            ImageView i = new ImageView(this);
            int id = getResources().getIdentifier(hero, "drawable", getPackageName());
            Drawable d = getResources().getDrawable(id);
            i.setImageDrawable(iu.resize(d, size.x));
            // i.setImageResource(id);

            i.setOnClickListener(imgClickListener);
            i.setPadding(2,0,2,0); //TODO pas propre
            i.setTag(hero); //pour identifier l'image plus facilement

            imLayout.addView(i);

            /*android.view.ViewGroup.LayoutParams layoutParams = i.getLayoutParams();
            layoutParams.width = 20;
            layoutParams.height = 20;
            i.setLayoutParams(layoutParams);*/
        }
    }

    //OnClickListener sur la liste complète pour savoir quel héros est choisi
    //TODO ajouter le même listener sur les images "conseil" pour simplifier les picks ?
    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(draftPickOrder == 0) {
                switchTeamsButton.setVisibility(View.GONE);
                imChoice1.setVisibility(View.VISIBLE);
                imChoice2.setVisibility(View.VISIBLE);
                imChoice3.setVisibility(View.VISIBLE);
            }

            if(draftPickOrder < 10) {

                String n1 = v.getTag().toString();

                if((n1.equals("cho") || n1.equals("gall")) && draftPickOrder < 8) {

                    Boolean doublePick = ((LinearLayout)imPickOrder[draftPickOrder].getParent()).getTag().toString().equals(((LinearLayout)imPickOrder[draftPickOrder+1].getParent()).getTag().toString());

                    if(doublePick) {
                        Log.i(TAG, "doublePick detected");

                        addPick("cho");
                        addPick("gall");
                        updateTeams();
                    }
                } else if(!n1.equals("cho") && !n1.equals("gall")) {
                    addPick(n1);
                    updateTeams();
                }

                iu.lightNextPicks(imPickOrder, draftPickOrder);

                if(draftPickOrder == 10) {
                    endDraft();
                }
            }
        }
    };


    //Permet d'annuler le dernier pick effectué
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(draftPickOrder == 0) {
                Log.w(TAG, "Can't cancel last pick");
                return true;
            }

            String n1 = tvPickOrder[draftPickOrder-1].getText().toString();
            Log.i(TAG, "Cancel pick " + n1);

            cancelPick();
            if(n1.equals("cho") || n1.equals("gall")) { cancelPick(); }

            updateTeams();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setTeamOrder(int alliesTeamOrder) {

        if(alliesTeamOrder==1) {

            imPickOrder = new ImageView[] {imAllies1,imEnnemies1,imEnnemies2,imAllies2,imAllies3,imEnnemies3,imEnnemies4,imAllies4,imAllies5,imEnnemies5};
            imAllies1.setImageDrawable(topickAllies_drawable);
        } else {
            imPickOrder = new ImageView[] {imEnnemies1,imAllies1,imAllies2,imEnnemies2,imEnnemies3,imAllies3,imAllies4,imEnnemies4,imEnnemies5,imAllies5};
            imEnnemies1.setImageDrawable(topickEnnemies_drawable);
        }

    }

    public void setTvTeamOrder(int alliesTeamOrder) {

        if(alliesTeamOrder==1) {
            tvPickOrder = new TextView[] {tvAllies1,tvennemies1,tvennemies2,tvAllies2,tvAllies3,tvennemies3,tvennemies4,tvAllies4,tvAllies5,tvennemies5};
        } else {
            tvPickOrder = new TextView[] {tvennemies1,tvAllies1,tvAllies2,tvennemies2,tvennemies3,tvAllies3,tvAllies4,tvennemies4,tvennemies5,tvAllies5};
        }

    }


    private void addPick(String heroName) {
        int idHero = getResources().getIdentifier(heroName, "drawable", getPackageName());
        imPickOrder[draftPickOrder].setImageResource(idHero);
        tvPickOrder[draftPickOrder].setText(heroName);

        //On cache l'image du héros sélectionner pour pas pouvoir le sélectionner plusieurs fois
        View currView = findViewById(android.R.id.content);
        View im = currView.findViewWithTag(heroName);
        im.setVisibility(View.GONE);

        draftPickOrder++;
    }

    private void cancelPick() {
        int idDefault;
        draftPickOrder--;

        String tagParent = ((LinearLayout)imPickOrder[draftPickOrder].getParent()).getTag().toString();
        String n = tvPickOrder[draftPickOrder].getText().toString();

        //Permet de savoir dans quel équipe on enlève un membre
        if(tagParent.equals("allies")) {
            idDefault = getResources().getIdentifier("default1", "drawable", getPackageName());
        } else {
            idDefault = getResources().getIdentifier("default2", "drawable", getPackageName());
        }
        imPickOrder[draftPickOrder].setImageResource(idDefault);
        tvPickOrder[draftPickOrder].setText(getResources().getString(R.string.defaultName));

        //On reaffiche l'image du héros sélectionner pour pouvoir le sélectionner à nouveau
        View currView = findViewById(android.R.id.content);
        View im = currView.findViewWithTag(n);
        im.setVisibility(View.VISIBLE);
    }

    private void updateTeams() {
        String[] ennemyTeam = {tvennemies1.getText().toString(), tvennemies2.getText().toString(), tvennemies3.getText().toString(), tvennemies4.getText().toString(), tvennemies5.getText().toString()};
        String[] alliesTeam = {tvAllies1.getText().toString(), tvAllies2.getText().toString(), tvAllies3.getText().toString(), tvAllies4.getText().toString(), tvAllies5.getText().toString()};

        if(!ennemyTeam[0].equals(getResources().getString(R.string.defaultName))) {
            jsonRequests.getBestAgainstTeam(ennemyTeam, alliesTeam);
        }
    }

    private void endDraft() {

        imChoice1.setVisibility(View.GONE);
        imChoice2.setVisibility(View.GONE);
        imChoice3.setVisibility(View.GONE);

        resetButton.setVisibility(View.VISIBLE);
    }

    View.OnClickListener newDraftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    };

    View.OnClickListener switchTeamsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            iu.resetNextPicks(imPickOrder);

            if(imPickOrder[0].equals(imAllies1)) {
                setTeamOrder(0);
                setTvTeamOrder(0);
            } else {
                setTeamOrder(1);
                setTvTeamOrder(1);
            }

        }
    };
}
