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

import java.util.*;

@Warmup(iterations = 10)
@Measurement(iterations = 30)
@Fork(1)
public class SocialNetworkBenchmark {

    @State(Scope.Thread)
    public static class Routers {

        @Setup(Level.Iteration)
        public void doSetup() {
            initializeTree();
            initializeImmutableECTree();
            initializeECTree();
            initializeList();

            // Now I initialize the list of compatible paths to match against
            compatiblePaths = new ArrayList<>();
            probabilitiesForRandom = new ArrayList<>();

            compatiblePaths.add("/feed");
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);
            probabilitiesForRandom.add(0);

            compatiblePaths.add("/users/popular");
            probabilitiesForRandom.add(1);
            probabilitiesForRandom.add(1);

            compatiblePaths.add("/users/user1");
            probabilitiesForRandom.add(2);
            probabilitiesForRandom.add(2);
            probabilitiesForRandom.add(2);
            probabilitiesForRandom.add(2);

            compatiblePaths.add("/users/user1/events");
            probabilitiesForRandom.add(3);

            compatiblePaths.add("/users/user1/likes");
            probabilitiesForRandom.add(4);

            compatiblePaths.add("/users/user1/pages");
            probabilitiesForRandom.add(5);

            compatiblePaths.add("/users/user1/friends");
            probabilitiesForRandom.add(6);

            compatiblePaths.add("/users/user1/feed");
            probabilitiesForRandom.add(7);
            probabilitiesForRandom.add(7);

            compatiblePaths.add("/posts/popular");
            probabilitiesForRandom.add(8);
            probabilitiesForRandom.add(8);

            compatiblePaths.add("/posts/post1");
            probabilitiesForRandom.add(9);

            compatiblePaths.add("/posts/post1/tagged");
            probabilitiesForRandom.add(10);

            compatiblePaths.add("/posts/post1/photos");
            probabilitiesForRandom.add(11);

            compatiblePaths.add("/posts/post1/photos/photo1");
            probabilitiesForRandom.add(12);

            compatiblePaths.add("/events/popular");
            probabilitiesForRandom.add(13);
            probabilitiesForRandom.add(13);
            probabilitiesForRandom.add(13);

            compatiblePaths.add("/events/event1");
            probabilitiesForRandom.add(14);
            probabilitiesForRandom.add(14);

            compatiblePaths.add("/events/event1/partecipants");
            probabilitiesForRandom.add(15);
            probabilitiesForRandom.add(15);

            compatiblePaths.add("/events/event1/invited");
            probabilitiesForRandom.add(16);

            compatiblePaths.add("/events/event1/feed");
            probabilitiesForRandom.add(17);
            probabilitiesForRandom.add(17);

            compatiblePaths.add("/pages/popular");
            probabilitiesForRandom.add(18);
            probabilitiesForRandom.add(18);
            probabilitiesForRandom.add(18);
            probabilitiesForRandom.add(18);

            compatiblePaths.add("/pages/page1");
            probabilitiesForRandom.add(19);
            probabilitiesForRandom.add(19);
            probabilitiesForRandom.add(19);
            probabilitiesForRandom.add(19);

            compatiblePaths.add("/pages/page1/likes");
            probabilitiesForRandom.add(20);

            compatiblePaths.add("/pages/page1/events");
            probabilitiesForRandom.add(21);

            compatiblePaths.add("/pages/page1/feed");
            probabilitiesForRandom.add(22);
            probabilitiesForRandom.add(22);
            probabilitiesForRandom.add(22);

            compatiblePaths.add("/pages/page1/feed/post1");
            probabilitiesForRandom.add(23);

            Collections.shuffle(probabilitiesForRandom);
        }

        private void initializeTree() {
            // I manually create the tree because the insertion algorithm It's dumb
            // Standard tree
            // Root level
            DummyNode root = new DummyNode("");
            tree = new TreeRouter(root);

            // First level
            DummyNode n11 = new DummyNode("/feed");
            DummyNode n12 = new DummyNode("/users");
            DummyNode n13 = new DummyNode("/posts");
            DummyNode n14 = new DummyNode("/events");
            DummyNode n15 = new DummyNode("/pages");

            root.addNode(n11);
            root.addNode(n12);
            root.addNode(n13);
            root.addNode(n14);
            root.addNode(n15);

            // Second level
            DummyNode n12_1 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n12_2 = new DummyNode("/popular");
            DummyNode n13_1 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n13_2 = new DummyNode("/popular");
            DummyNode n14_1 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n14_2 = new DummyNode("/popular");
            DummyNode n15_1 = new DummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            DummyNode n15_2 = new DummyNode("/popular");

            n12.addNode(n12_1);
            n12.addNode(n12_2);
            n13.addNode(n13_1);
            n13.addNode(n13_2);
            n14.addNode(n14_1);
            n14.addNode(n14_2);
            n15.addNode(n15_1);
            n15.addNode(n15_2);

            // Third level
            DummyNode n12_1_1 = new DummyNode("/events");
            DummyNode n12_1_2 = new DummyNode("/likes");
            DummyNode n12_1_3 = new DummyNode("/pages");
            DummyNode n12_1_4 = new DummyNode("/friends");
            DummyNode n12_1_5 = new DummyNode("/feed");

            n12_1.addNode(n12_1_1);
            n12_1.addNode(n12_1_2);
            n12_1.addNode(n12_1_3);
            n12_1.addNode(n12_1_4);
            n12_1.addNode(n12_1_5);

            DummyNode n13_1_1 = new DummyNode("/tagged");
            DummyNode n13_1_2 = new DummyNode("/photos");

            n13_1.addNode(n13_1_1);
            n13_1.addNode(n13_1_2);

            DummyNode n14_1_1 = new DummyNode("/partecipants");
            DummyNode n14_1_2 = new DummyNode("/invited");
            DummyNode n14_1_3 = new DummyNode("/feed");

            n14_1.addNode(n14_1_1);
            n14_1.addNode(n14_1_2);
            n14_1.addNode(n14_1_3);

            DummyNode n15_1_1 = new DummyNode("/likes");
            DummyNode n15_1_2 = new DummyNode("/events");
            DummyNode n15_1_3 = new DummyNode("/feed");

            n15_1.addNode(n15_1_1);
            n15_1.addNode(n15_1_2);
            n15_1.addNode(n15_1_3);

            // Forth level
            DummyNode n13_1_2_1 = new DummyNode("\\/(?<photoid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n13_1_2.addNode(n13_1_2_1);

            DummyNode n15_1_3_1 = new DummyNode("\\/(?<postid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n15_1_3.addNode(n15_1_3_1);
        }

        private void initializeImmutableECTree() {
            // Root level
            ImmutableECDummyNode root = new ImmutableECDummyNode("");
            immutableECTreeRouter = new ImmutableECTreeRouter(root);

            // First level
            ImmutableECDummyNode n11 = new ImmutableECDummyNode("/feed");
            ImmutableECDummyNode n12 = new ImmutableECDummyNode("/users");
            ImmutableECDummyNode n13 = new ImmutableECDummyNode("/posts");
            ImmutableECDummyNode n14 = new ImmutableECDummyNode("/events");
            ImmutableECDummyNode n15 = new ImmutableECDummyNode("/pages");

            root.addNode(n11);
            root.addNode(n12);
            root.addNode(n13);
            root.addNode(n14);
            root.addNode(n15);

            // Second level
            ImmutableECDummyNode n12_1 = new ImmutableECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ImmutableECDummyNode n12_2 = new ImmutableECDummyNode("/popular");
            ImmutableECDummyNode n13_1 = new ImmutableECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ImmutableECDummyNode n13_2 = new ImmutableECDummyNode("/popular");
            ImmutableECDummyNode n14_1 = new ImmutableECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ImmutableECDummyNode n14_2 = new ImmutableECDummyNode("/popular");
            ImmutableECDummyNode n15_1 = new ImmutableECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ImmutableECDummyNode n15_2 = new ImmutableECDummyNode("/popular");

            n12.addNode(n12_1);
            n12.addNode(n12_2);
            n13.addNode(n13_1);
            n13.addNode(n13_2);
            n14.addNode(n14_1);
            n14.addNode(n14_2);
            n15.addNode(n15_1);
            n15.addNode(n15_2);

            // Third level
            ImmutableECDummyNode n12_1_1 = new ImmutableECDummyNode("/events");
            ImmutableECDummyNode n12_1_2 = new ImmutableECDummyNode("/likes");
            ImmutableECDummyNode n12_1_3 = new ImmutableECDummyNode("/pages");
            ImmutableECDummyNode n12_1_4 = new ImmutableECDummyNode("/friends");
            ImmutableECDummyNode n12_1_5 = new ImmutableECDummyNode("/feed");

            n12_1.addNode(n12_1_1);
            n12_1.addNode(n12_1_2);
            n12_1.addNode(n12_1_3);
            n12_1.addNode(n12_1_4);
            n12_1.addNode(n12_1_5);

            ImmutableECDummyNode n13_1_1 = new ImmutableECDummyNode("/tagged");
            ImmutableECDummyNode n13_1_2 = new ImmutableECDummyNode("/photos");

            n13_1.addNode(n13_1_1);
            n13_1.addNode(n13_1_2);

            ImmutableECDummyNode n14_1_1 = new ImmutableECDummyNode("/partecipants");
            ImmutableECDummyNode n14_1_2 = new ImmutableECDummyNode("/invited");
            ImmutableECDummyNode n14_1_3 = new ImmutableECDummyNode("/feed");

            n14_1.addNode(n14_1_1);
            n14_1.addNode(n14_1_2);
            n14_1.addNode(n14_1_3);

            ImmutableECDummyNode n15_1_1 = new ImmutableECDummyNode("/likes");
            ImmutableECDummyNode n15_1_2 = new ImmutableECDummyNode("/events");
            ImmutableECDummyNode n15_1_3 = new ImmutableECDummyNode("/feed");

            n15_1.addNode(n15_1_1);
            n15_1.addNode(n15_1_2);
            n15_1.addNode(n15_1_3);

            // Forth level
            ImmutableECDummyNode n13_1_2_1 = new ImmutableECDummyNode("\\/(?<photoid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n13_1_2.addNode(n13_1_2_1);

            ImmutableECDummyNode n15_1_3_1 = new ImmutableECDummyNode("\\/(?<postid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n15_1_3.addNode(n15_1_3_1);

            immutableECTreeRouter.lockTree();
        }

        private void initializeECTree() {
            // Root level
            ECDummyNode root = new ECDummyNode("");
            ecTreeRouter = new ECTreeRouter(root);

            // First level
            ECDummyNode n11 = new ECDummyNode("/feed");
            ECDummyNode n12 = new ECDummyNode("/users");
            ECDummyNode n13 = new ECDummyNode("/posts");
            ECDummyNode n14 = new ECDummyNode("/events");
            ECDummyNode n15 = new ECDummyNode("/pages");

            root.addNode(n11);
            root.addNode(n12);
            root.addNode(n13);
            root.addNode(n14);
            root.addNode(n15);

            // Second level
            ECDummyNode n12_1 = new ECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ECDummyNode n12_2 = new ECDummyNode("/popular");
            ECDummyNode n13_1 = new ECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ECDummyNode n13_2 = new ECDummyNode("/popular");
            ECDummyNode n14_1 = new ECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ECDummyNode n14_2 = new ECDummyNode("/popular");
            ECDummyNode n15_1 = new ECDummyNode("\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            ECDummyNode n15_2 = new ECDummyNode("/popular");

            n12.addNode(n12_1);
            n12.addNode(n12_2);
            n13.addNode(n13_1);
            n13.addNode(n13_2);
            n14.addNode(n14_1);
            n14.addNode(n14_2);
            n15.addNode(n15_1);
            n15.addNode(n15_2);

            // Third level
            ECDummyNode n12_1_1 = new ECDummyNode("/events");
            ECDummyNode n12_1_2 = new ECDummyNode("/likes");
            ECDummyNode n12_1_3 = new ECDummyNode("/pages");
            ECDummyNode n12_1_4 = new ECDummyNode("/friends");
            ECDummyNode n12_1_5 = new ECDummyNode("/feed");

            n12_1.addNode(n12_1_1);
            n12_1.addNode(n12_1_2);
            n12_1.addNode(n12_1_3);
            n12_1.addNode(n12_1_4);
            n12_1.addNode(n12_1_5);

            ECDummyNode n13_1_1 = new ECDummyNode("/tagged");
            ECDummyNode n13_1_2 = new ECDummyNode("/photos");

            n13_1.addNode(n13_1_1);
            n13_1.addNode(n13_1_2);

            ECDummyNode n14_1_1 = new ECDummyNode("/partecipants");
            ECDummyNode n14_1_2 = new ECDummyNode("/invited");
            ECDummyNode n14_1_3 = new ECDummyNode("/feed");

            n14_1.addNode(n14_1_1);
            n14_1.addNode(n14_1_2);
            n14_1.addNode(n14_1_3);

            ECDummyNode n15_1_1 = new ECDummyNode("/likes");
            ECDummyNode n15_1_2 = new ECDummyNode("/events");
            ECDummyNode n15_1_3 = new ECDummyNode("/feed");

            n15_1.addNode(n15_1_1);
            n15_1.addNode(n15_1_2);
            n15_1.addNode(n15_1_3);

            // Forth level
            ECDummyNode n13_1_2_1 = new ECDummyNode("\\/(?<photoid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n13_1_2.addNode(n13_1_2_1);

            ECDummyNode n15_1_3_1 = new ECDummyNode("\\/(?<postid>[a-zA-Z][a-zA-Z0-9]{3,20})", true);
            n15_1_3.addNode(n15_1_3_1);
        }

        private void initializeList() {
            // Now I initialize the list
            list = new ListRouter();

            list.add(new Route("/feed"));

            list.add(new Route("/users/popular"));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/events", true));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/likes", true));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/pages", true));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/friends", true));
            list.add(new Route("\\/users\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/feed", true));

            list.add(new Route("/posts/popular"));
            list.add(new Route("\\/posts\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/tagged", true));
            list.add(new Route("\\/posts\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/photos", true));
            list.add(new Route("\\/posts\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/photos\\/(?<photoid>[a-zA-Z][a-zA-Z0-9]{3,20})", true));

            list.add(new Route("/events/popular"));
            list.add(new Route("\\/events\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));
            list.add(new Route("\\/events\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/partecipants", true));
            list.add(new Route("\\/events\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/invited", true));
            list.add(new Route("\\/events\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/feed", true));

            list.add(new Route("/pages/popular"));
            list.add(new Route("\\/pages\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})", true));
            list.add(new Route("\\/pages\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/likes", true));
            list.add(new Route("\\/pages\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/events", true));
            list.add(new Route("\\/pages\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/feed", true));
            list.add(new Route("\\/pages\\/(?<id>[a-zA-Z][a-zA-Z0-9]{3,20})\\/feed\\/(?<postid>[a-zA-Z][a-zA-Z0-9]{3,20})", true));
        }

        @Setup(Level.Invocation)
        public void switchElement() {
            randomPath = compatiblePaths.get(
                    probabilitiesForRandom.get(
                            new Random().nextInt(probabilitiesForRandom.size())
                    )
            );
        }

        public boolean treeRouting(String path) {
            return tree.route(path);
        }

        public boolean immutableECTreeRouting(String path) {
            return immutableECTreeRouter.route(path);
        }

        public boolean ecTreeRouting(String path) {
            return ecTreeRouter.route(path);
        }

        public boolean skipListRouting(String path) {
            return list.route(path);
        }

        TreeRouter tree;
        ListRouter list;
        ImmutableECTreeRouter immutableECTreeRouter;
        ECTreeRouter ecTreeRouter;

        String randomPath;
        List<String> compatiblePaths;

        List<Integer> probabilitiesForRandom;
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void treeRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting(routers.randomPath));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void skipListRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting(routers.randomPath));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void immutableECTreeRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.immutableECTreeRouting(routers.randomPath));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @Measurement(iterations = 100)
    public void ecTreeRouting(Routers routers, Blackhole bh) {
        bh.consume(routers.ecTreeRouting(routers.randomPath));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1TreeRouting(Routers routers) {
        routers.treeRouting("/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1SkipListRouting(Routers routers) {
        routers.skipListRouting("/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2TreeRouting(Routers routers) {
        routers.treeRouting("/users/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1/pages");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1/pages");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1/pages");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1/pages");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1/friends");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1/friends");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1/friends");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1/friends");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8TreeRouting(Routers routers) {
        routers.treeRouting("/users/user1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/users/user1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/users/user1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8SkipListRouting(Routers routers) {
        routers.skipListRouting("/users/user1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9TreeRouting(Routers routers) {
        routers.treeRouting("/posts/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/posts/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/posts/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9SkipListRouting(Routers routers) {
        routers.skipListRouting("/posts/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10TreeRouting(Routers routers) {
        routers.treeRouting("/posts/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/posts/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/posts/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10SkipListRouting(Routers routers) {
        routers.skipListRouting("/posts/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11TreeRouting(Routers routers) {
        routers.treeRouting("/posts/post1/tagged");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/posts/post1/tagged");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/posts/post1/tagged");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11SkipListRouting(Routers routers) {
        routers.skipListRouting("/posts/post1/tagged");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12TreeRouting(Routers routers) {
        routers.treeRouting("/posts/post1/photos");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/posts/post1/photos");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/posts/post1/photos");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12SkipListRouting(Routers routers) {
        routers.skipListRouting("/posts/post1/photos");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13TreeRouting(Routers routers) {
        routers.treeRouting("/posts/post1/photos/photo1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/posts/post1/photos/photo1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/posts/post1/photos/photo1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13SkipListRouting(Routers routers) {
        routers.skipListRouting("/posts/post1/photos/photo1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14TreeRouting(Routers routers) {
        routers.treeRouting("/events/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/events/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/events/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14SkipListRouting(Routers routers) {
        routers.skipListRouting("/events/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15TreeRouting(Routers routers) {
        routers.treeRouting("/events/event1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/events/event1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/events/event1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15SkipListRouting(Routers routers) {
        routers.skipListRouting("/events/event1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16TreeRouting(Routers routers) {
        routers.treeRouting("/events/event1/partecipants");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/events/event1/partecipants");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/events/event1/partecipants");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16SkipListRouting(Routers routers) {
        routers.skipListRouting("/events/event1/partecipants");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17TreeRouting(Routers routers) {
        routers.treeRouting("/events/event1/invited");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/events/event1/invited");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/events/event1/invited");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17SkipListRouting(Routers routers) {
        routers.skipListRouting("/events/event1/invited");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18TreeRouting(Routers routers) {
        routers.treeRouting("/events/event1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/events/event1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/events/event1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18SkipListRouting(Routers routers) {
        routers.skipListRouting("/events/event1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19TreeRouting(Routers routers) {
        routers.treeRouting("/pages/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/popular");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20TreeRouting(Routers routers) {
        routers.treeRouting("/pages/page1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/page1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/page1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/page1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21TreeRouting(Routers routers) {
        routers.treeRouting("/pages/page1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/page1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/page1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/page1/likes");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22TreeRouting(Routers routers) {
        routers.treeRouting("/pages/page1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/page1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/page1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/page1/events");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23TreeRouting(Routers routers) {
        routers.treeRouting("/pages/page1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/page1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/page1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/page1/feed");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24TreeRouting(Routers routers) {
        routers.treeRouting("/pages/page1/feed/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24ECTreeRouting(Routers routers) {
        routers.ecTreeRouting("/pages/page1/feed/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24immutableECTreeRouting(Routers routers) {
        routers.immutableECTreeRouting("/pages/page1/feed/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24SkipListRouting(Routers routers) {
        routers.skipListRouting("/pages/page1/feed/post1");
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24ECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
        bh.consume(routers.ecTreeRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24immutableECTreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
        bh.consume(routers.immutableECTreeRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/feed/post1"));
    }

}
