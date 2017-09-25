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
import java.util.concurrent.ConcurrentSkipListSet;

@Warmup(iterations = 10)
@Measurement(iterations = 30)
@Fork(3)
public class SocialNetworkBenchmark {

    @State(Scope.Thread)
    public static class Routers {

        @Setup(Level.Iteration)
        public void doSetup() {
            // I manually create the tree because the insertion algorithm It's dumb
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

            // Now I initialize the list of compatible paths to mathc against
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

        @Setup(Level.Invocation)
        public void switchElement() {
            path = compatiblePaths.get(i);
            i = (i + 1) % 7;

            randomPath = compatiblePaths.get(
                    probabilitiesForRandom.get(
                            new Random().nextInt(probabilitiesForRandom.size())
                    )
            );
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

        List<Integer> probabilitiesForRandom;
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
        bh.consume(routers.treeRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route1List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route2List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route3List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route4List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route5List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route6List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1/pages"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route7List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1/friends"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route8List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/users/user1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route9List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/posts/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route10List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/posts/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route11List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/posts/post1/tagged"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route12List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/posts/post1/photos"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route13List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/posts/post1/photos/photo1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route14List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/events/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route15List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/events/event1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route16List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/events/event1/partecipants"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route17List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/events/event1/invited"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route18List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/events/event1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route19List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/popular"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route20List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/page1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route21List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/page1/likes"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route22List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/page1/events"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route23List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/page1/feed"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24TreeWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
        bh.consume(routers.treeRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24ListWithLoad(Routers routers, Blackhole bh) {
        for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
        bh.consume(routers.skipListRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24Tree(Routers routers, Blackhole bh) {
        bh.consume(routers.treeRouting("/pages/page1/feed/post1"));
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void route24List(Routers routers, Blackhole bh) {
        bh.consume(routers.skipListRouting("/pages/page1/feed/post1"));
    }



}
