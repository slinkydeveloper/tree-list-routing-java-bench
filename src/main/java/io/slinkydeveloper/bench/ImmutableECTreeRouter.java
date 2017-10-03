package io.slinkydeveloper.bench;

/**
 * @author Francesco Guardiani @slinkydeveloper
 */
public class ImmutableECTreeRouter {
    ImmutableECDummyNode root;

    public ImmutableECTreeRouter(ImmutableECDummyNode root) {
        this.root = root;
    }

    public boolean route(String route) {
        return this.root.route(route);
    }

    public void lockTree() {
        root.setImmutable();
    }
}
