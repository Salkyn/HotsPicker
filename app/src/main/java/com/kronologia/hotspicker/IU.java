package com.kronologia.hotspicker;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Maxence on 07/04/2016.
 */
public class IU {

    private Resources res;
    private String packageName;
    private Activity act;
    private String TAG;

    public IU(Resources r, String pack, Activity a) {
        this.res = r;
        this.packageName = pack;
        this.act = a;
        this.TAG = IU.class.getSimpleName();
    }

    //Maj de l'interface selon les meilleurs héros obtenus
    public void updateIuTop3(String name1, String name2, String name3) {

        ImageView imChoice1 = (ImageView) act.findViewById(R.id.imageViewChoice1);
        ImageView imChoice2 = (ImageView) act.findViewById(R.id.imageViewChoice2);
        ImageView imChoice3 = (ImageView) act.findViewById(R.id.imageViewChoice3);

        int idHero = res.getIdentifier(name1.toLowerCase(), "drawable", packageName);
        imChoice1.setTag(name1);
        imChoice1.setImageResource(idHero);

        int idHero2 = res.getIdentifier(name2.toLowerCase(), "drawable", packageName);
        imChoice2.setTag(name2);
        imChoice2.setImageResource(idHero2);

        int idHero3 = res.getIdentifier(name3.toLowerCase(), "drawable", packageName);
        imChoice3.setTag(name3);
        imChoice3.setImageResource(idHero3);
    }

    public void resetNextPicks(ImageView[] ivs) {

        int idDrawable_Allies = res.getIdentifier("default1", "drawable", packageName);
        Drawable allies = res.getDrawable(idDrawable_Allies);

        int idDrawable_Ennemies = res.getIdentifier("default2", "drawable", packageName);
        Drawable ennemies = res.getDrawable(idDrawable_Ennemies);

        for(int i = 0 ; i < 10 ; i++) {
            String tagParent = ((LinearLayout)ivs[i].getParent()).getTag().toString();

            //Permet de savoir dans quel équipe on enlève un membre
            if(tagParent.equals("allies")) {
                ivs[i].setImageDrawable(allies);
            } else {
                ivs[i].setImageDrawable(ennemies);
            }
        }
    }


    public Drawable resize(Drawable image, int width) {

        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width/10, width/10, false);
        return new BitmapDrawable(res, bitmapResized);
    }

    public void lightNextPicks(ImageView[] ivs, int currentPickNumber) {
        if (currentPickNumber == 10) {
            return;
        }

        int idDrawable_toPickAllies = res.getIdentifier("default1_c", "drawable", packageName);
        Drawable allies = res.getDrawable(idDrawable_toPickAllies);

        int idDrawable_toPickEnnemies = res.getIdentifier("default2_c", "drawable", packageName);
        Drawable ennemies = res.getDrawable(idDrawable_toPickEnnemies);

        String tagCurrentParent = ((LinearLayout)ivs[currentPickNumber].getParent()).getTag().toString();

        if(tagCurrentParent.equals("allies")) {
            ivs[currentPickNumber].setImageDrawable(allies);
        } else {
            ivs[currentPickNumber].setImageDrawable(ennemies);
        }

        if(currentPickNumber != 9) {
            String tagNextParent = ((LinearLayout)ivs[currentPickNumber+1].getParent()).getTag().toString();
            if(tagCurrentParent.equals(tagNextParent)) {
                if(tagCurrentParent.equals("allies")) {
                    ivs[currentPickNumber+1].setImageDrawable(allies);
                } else {
                    ivs[currentPickNumber+1].setImageDrawable(ennemies);
                }
            }
        }
    }
}
