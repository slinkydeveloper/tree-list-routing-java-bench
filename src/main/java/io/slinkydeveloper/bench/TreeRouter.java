package io.slinkydeveloper.bench;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class TreeRouter {
    DummyNode root;

    public TreeRouter(DummyNode root) {
        this.root = root;
    }

    public boolean route(String route) {
        return this.root.route(route);
    }
}
