package com.kronologia.hotspicker;

/**
 * Created by Maxence on 06/04/2016.
 */
public class HeroesGestion {

    public static String formatHeroName(String name) {
        switch(name) {
            case("The Lost Vikings"):
                return "lostvikings";
            case("Sgt. Hammer"):
                return "sgthammer";
            case("Lt. Morales"):
                return "ltmorales";
            case("E.T.C."):
                return "etc";
            case("The Butcher"):
                return "thebutcher";
            case("Li-Ming"):
                return "liming";
            case("Kael'Thas"):
                return "kaelthas";
            default:
                return name;
        }
    }
}
