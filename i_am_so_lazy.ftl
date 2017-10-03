<#list list as i>
@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}TreeRouting(Routers routers) {
routers.treeRouting("${i}");
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}ECTreeRouting(Routers routers) {
routers.ecTreeRouting("${i}");
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}immutableECTreeRouting(Routers routers) {
routers.immutableECTreeRouting("${i}");
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}SkipListRouting(Routers routers) {
routers.skipListRouting("${i}");
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}TreeWithLoad(Routers routers, Blackhole bh) {
for (int i = 0; i < 10; i++) bh.consume(routers.treeRouting(routers.randomPath));
bh.consume(routers.treeRouting("${i}"));
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}ECTreeWithLoad(Routers routers, Blackhole bh) {
for (int i = 0; i < 10; i++) bh.consume(routers.ecTreeRouting(routers.randomPath));
bh.consume(routers.ecTreeRouting("${i}"));
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}immutableECTreeWithLoad(Routers routers, Blackhole bh) {
for (int i = 0; i < 10; i++) bh.consume(routers.immutableECTreeRouting(routers.randomPath));
bh.consume(routers.immutableECTreeRouting("${i}"));
}

@Benchmark @BenchmarkMode(Mode.Throughput)
public void route${i?counter}ListWithLoad(Routers routers, Blackhole bh) {
for (int i = 0; i < 10; i++) bh.consume(routers.skipListRouting(routers.randomPath));
bh.consume(routers.skipListRouting("${i}"));
}

</#list>