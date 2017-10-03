package io.slinkydeveloper.bench;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ECTreeRouter {
    ECDummyNode root;

    public ECTreeRouter(ECDummyNode root) {
        this.root = root;
    }

    public boolean route(String route) {
        return this.root.route(route);
    }
}
