package webapp;

import java.util.HashMap;

import static spark.Spark.*;
import static utils.ViewUtils.*;

public class WebMain {
    public static void main(String[] args) {

        /* static files */
        staticFiles.location("/static");

        /* servlet(s) */
        get("/demonstrator", WebServlets::demonstrate);

    }
}
