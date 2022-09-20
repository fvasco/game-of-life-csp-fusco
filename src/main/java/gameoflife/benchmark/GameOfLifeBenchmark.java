package gameoflife.benchmark;

import gameoflife.DispatcherType;
import gameoflife.ExecutionArgs;
import gameoflife.GameOfLife;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
public class GameOfLifeBenchmark {

    @Param({"10", "100"})
    private int padding;

    @Param({"1", "2147483647"}) // size =1, unlimited
    private int channelSize;

    @Param({"DEFAULT", "COMMON_POOL", "VIRTUAL", "VIRTUAL_IMMEDIATE"})
    private String dispatcherType;

    private GameOfLife gameOfLife;

    @Setup
    public void setup() {
        ExecutionArgs args = ExecutionArgs.create(padding, DispatcherType.valueOf(dispatcherType), channelSize);
        gameOfLife = GameOfLife.create(args);
        gameOfLife.startCells();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public boolean[][] benchmark() {
        return gameOfLife.calculateFrameBlocking();
    }
}
