/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.slinkydeveloper.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

@Warmup(iterations = 15)
@Measurement(iterations = 30)
@Fork(1)
public class ECommerceBenchmark {

    @State(Scope.Thread)
    public static class Routers {

        @Setup(Level.Iteration)
        public void doSetup() {
            // I manually create the tree because the insertion algorithm It's dumb
            // Root level
            DummyNode root = new DummyNode("");
            tree = new TreeRouter(root);

            // First level
            DummyNode n11 = new DummyNode("/users");
            DummyNode n12 = new DummyNode("/products");
            DummyNode n13 = new DummyNode("/health");
            DummyNode n14 = new DummyNode("/cart");

            root.addNode(n11);
            root.addNode(n12);
            root.addNode(n13);
            root.addNode(n14);

            // Second level
            DummyNode n21 = new DummyNode("/newUser");
            DummyNode n22 = new DummyNode("/removeUser");
            DummyNode n23 = new DummyNode("/newFacebookUser");
            DummyNode n24 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n25 = new DummyNode("/buy");
            DummyNode n26 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);

            n11.addNode(n21);
            n11.addNode(n22);
            n11.addNode(n23);
            n11.addNode(n24);
            n12.addNode(n25);
            n14.addNode(n26);

            // Third level
            DummyNode n31 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n32 = new DummyNode("/remove");
            DummyNode n33 = new DummyNode("/modify");

            n25.addNode(n31);
            n26.addNode(n32);
            n26.addNode(n33);

            // Now I initialize the list
            list = new ListRouter();

            list.add(new Route("/users"));
            list.add(new Route("/users/newUser"));
            list.add(new Route("/users/removeUser"));
            list.add(new Route("/users/newFacebookUser"));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));

            list.add(new Route("/products"));
            list.add(new Route("/products/buy"));
            list.add(new Route("\\/products\\/buy\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));

            list.add(new Route("/health"));

            list.add(new Route("/cart"));
            list.add(new Route("\\/cart\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));
            list.add(new Route("\\/cart\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/remove", true));
            list.add(new Route("\\/cart\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/modify", true));

            // Now I initialize the list of compatible paths to mathc against
            compatiblePaths = new ArrayList<>();
            compatiblePaths.add("/users/newUser");
            compatiblePaths.add("/users/removeUser");
            compatiblePaths.add("/users/newFacebookUser");
            compatiblePaths.add("/users/superUser");
            compatiblePaths.add("/products/buy/helloAAA");
            compatiblePaths.add("/health");
            compatiblePaths.add("/cart/helloaaa/remove");
            compatiblePaths.add("/cart/ahuicas/modify");
        }

        @Setup(Level.Invocation)
        public void switchElement() {
            path = compatiblePaths.get(i);
            i = (i + 1) % 7;

            randomPath = compatiblePaths.get(new Random().nextInt(compatiblePaths.size()));
        }

        public boolean treeRouting() {
            return tree.route(path);
        }

        public boolean skipListRouting() {
            return list.route(path);
        }

        public boolean treeRouting(String path) {
            return tree.route(path);
        }

        public boolean skipListRouting(String path) {
            return list.route(path);
        }

        TreeRouter tree;
        ListRouter list;

        int i = 0;
        String path;
        String randomPath;
        List<String> compatiblePaths;
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void treeRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting());
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void skipListRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting());
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/newUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/newUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/newUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/newUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/removeUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/removeUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/removeUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/removeUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/newFacebookUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/newFacebookUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/newFacebookUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/newFacebookUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/superUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/superUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/superUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/superUser"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/products/buy/productA"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/products/buy/productA"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/products/buy/productA"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/products/buy/productA"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/health"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/health"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/health"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/health"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/cart/cartA/remove"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/cart/cartA/remove"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/cart/cartA/remove"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/cart/cartA/remove"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/cart/cartB/modify"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/cart/cartB/modify"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/cart/cartB/modify"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/cart/cartB/modify"));
    }



}
