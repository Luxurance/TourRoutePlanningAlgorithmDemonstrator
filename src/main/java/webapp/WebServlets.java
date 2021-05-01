package webapp;

import spark.Request;
import spark.Response;
import trp.LondonRouting;
import utils.LondonPlaceNumMappings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static utils.ViewUtils.render;

public class WebServlets {

    public static String demonstrate(Request request, Response response) throws IOException {
        /* get parameters */
        String source      = request.queryParams("source");
        String destination = request.queryParams("destination");
        String routeNumStr = request.queryParams("routeNum");
        /* default index page request */
        if (source == null || destination == null || routeNumStr == null
                || !isValidPlace(source) || !isValidPlace(destination)) {
            return render(new HashMap<>(), "index");
        }

        /* routing processing request */
        int routeNum = Integer.parseInt(routeNumStr);
        /* initialise and process the algorithm */
        LondonRouting routing = new LondonRouting("TRP", "TRP", source, destination, routeNum);
        List<List<String>> routes = routing.getTrajectories();

        return render(new HashMap<String, Object>(){{
            put("routes", routes);
        }}, "index");
    }

    private static boolean isValidPlace(String placeName) {
        return LondonPlaceNumMappings.placeNumMap.containsKey(placeName);
    }
}
