package utils;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class LondonPlaceNumMappings {

    public static final BidiMap<String, Integer> placeNumMap = new DualHashBidiMap<String, Integer>(){{
        put("Amersham", 0);
        put("Chorleywood", 1);
        put("Watford", 2);
        put("Eastcote", 3);
        put("Alperton", 4);
        put("Acton Town", 5);
        put("Gunnersbury", 6);
        put("Kew Gardens", 7);
        put("Earl's Court", 8);
        put("Barons Court", 9);
        put("Colliers Wood", 10);
        put("Balham", 11);
        put("Brixton", 12);
        put("Canons Park", 13);
        put("Baker Street", 14);
        put("Bermondsey", 15);
        put("Brent Cross", 16);
        put("Belsize Park", 17);
        put("Angel", 18);
        put("Arnos Grove", 19);
        put("Bound Green", 20);
        put("Manor House", 21);
        put("Blackhorse Road", 22);
        put("Buckhurst Hill", 23);
        put("Barkingside", 24);
        put("Elm Park", 25);
        put("Barking", 26);
        put("Canning Town", 27);
        put("North Greenwich", 28);
        put("Aldgate East", 29);
        put("Aldgate", 30);
    }};

    public static final BidiMap<Integer, String> numPlaceMap = placeNumMap.inverseBidiMap();

}
