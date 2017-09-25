package io.slinkydeveloper.bench;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ListRouter {

    ConcurrentSkipListSet<Route> routes;

    public ListRouter() {
        routes = new ConcurrentSkipListSet<>();
    }

    public void add(Route r) {
        routes.add(r);
    }

    public boolean route(String path) {
        for (Route route : routes) {
            if (route.route(path))
                return true;
        }
        return false;
    }
}
