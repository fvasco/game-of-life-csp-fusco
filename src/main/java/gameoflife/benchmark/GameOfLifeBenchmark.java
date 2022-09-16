package gameoflife.benchmark;

import gameoflife.ExecutionArgs;
import gameoflife.GameOfLife;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
public class GameOfLifeBenchmark {

    @Param({"5", "25", "100"}) // 874, 5074, 49324 cells
    private int padding;

    @Param({"true", "false"})
    private boolean useVirtualThreads;

    private GameOfLife gameOfLife;

    @Setup
    public void setup() {
        ExecutionArgs args = ExecutionArgs.create(padding, useVirtualThreads);
        gameOfLife = GameOfLife.create(args);
        gameOfLife.startCells();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public boolean[][] benchmark() {
        return gameOfLife.calculateFrameBlocking();
    }
}
