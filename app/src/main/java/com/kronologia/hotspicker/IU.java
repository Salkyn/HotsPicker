package com.kronologia.hotspicker;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.ImageView;

/**
 * Created by Maxence on 07/04/2016.
 */
public class IU {

    private Resources res;
    private String packageName;
    private Activity act;

    public IU(Resources r, String pack, Activity a) {
        this.res = r;
        this.packageName = pack;
        this.act = a;
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
}
