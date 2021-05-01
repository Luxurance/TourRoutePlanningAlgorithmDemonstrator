package trp;

import trp.LondonRouting;

import java.io.IOException;

public class RoutingMain {
    public static void main(String[] args) throws IOException {
        LondonRouting londonRouting = new LondonRouting("TRP", "TRP", "Amersham", "Angel", 3);
        System.out.println(londonRouting.getTrajectories());
    }
}
