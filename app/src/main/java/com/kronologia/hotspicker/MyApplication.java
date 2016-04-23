package com.kronologia.hotspicker;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Maxence on 22/04/2016.
 */
public class MyApplication extends Application {

    private static String[] heroNames = {"falstad","gall","greymane","illidan","jaina","kaelthas",
            "kerrigan","lunara","nova","raynor","thebutcher","thrall","tychus",
            "valla","zeratul","abathur","azmodan","gazlowe","lostvikings","murky",
            "nazeebo","sgthammer","sylvanas","zagara","brightwing","kharazim",
            "lili","ltmorales","malfurion","rehgar","tassadar","tyrande","uther",
            "anubarak","artanis","arthas","chen","cho","diablo","etc","johanna",
            "leoric","muradin","rexxar","sonya","stitches","tyrael","liming","xul","dehaka",
            "tracer"};

    private static List<String> removedHeroes = new ArrayList<String>();

    public static void orderHeroNames() {
        Arrays.sort(heroNames);
    }

    public static String[] getHeroNames() { return heroNames; }

    public static List<String> getRemovedHeroes() { return removedHeroes; }

    public static void removeHero(String name) {
        removedHeroes.add(HeroesGestion.formatHeroName(name));
        Log.d("MyApp", removedHeroes.toString());
    }

    public static void moveBackHero(String name) {
        //TODO remove "name" from removedHeroes
        //removedHeroes.remove(HeroesGestion.formatHeroName(name));
    }

}
