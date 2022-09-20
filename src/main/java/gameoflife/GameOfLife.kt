package gameoflife

import gameoflife.domain.Cell
import gameoflife.domain.ChannelsGrid
import gameoflife.domain.Dimensions
import gameoflife.ui.PatternParser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

class GameOfLife(
    val dimensions: Dimensions,
    seed: Array<BooleanArray>,
    private val period: Long,
    val gridChannel: Channel<Array<BooleanArray>>,
    private val logRate: Boolean,
    dispatcher: CoroutineDispatcher,
    channelSize: Int = 1
) : CoroutineScope {

    private val cells = ArrayList<Cell>()
    private val tickChannels: MutableList<Channel<Unit>>
    private val resultChannels = ChannelsGrid<Boolean>(dimensions)
    private var lastStatsDump = System.nanoTime()
    private var framesCount = 0L
    override val coroutineContext: CoroutineContext = Job() + dispatcher

    init {
        tickChannels = ArrayList(dimensions.rows * dimensions.cols)
        val grid = Array(dimensions.rows) { r ->
            Array(dimensions.cols) { c ->
                val tickChannel = Channel<Unit>(channelSize)
                tickChannels += tickChannel
                Cell(seed[r][c], tickChannel, resultChannels[r, c])
            }
        }
        dimensions.forEachRowCol { r: Int, c: Int -> cells.add(grid[r][c]) }
        dimensions.forEachRowCol { r: Int, c: Int ->
            dimensions.forEachNeighbor(r, c) { ri: Int, ci: Int ->
                val ch = Channel<Boolean>(channelSize)
                grid[r][c].addInChannel(ch)
                grid[ri][ci].addOutChannel(ch)
            }
        }
    }

    fun start() {
        startCells()
        startGame()
    }

    fun startCells() {
        cells.forEach { cell ->
            launch(start = CoroutineStart.UNDISPATCHED) { cell.run() }
        }
    }

    fun startGame() {
        launch { run() }
    }

    private suspend fun run() {
        while (true) {
            calculateFrame()
        }
    }

    suspend fun calculateFrame(): Array<BooleanArray> {
        tickChannels.forEach { it.send(Unit) }
        val grid = Array(dimensions.rows) { r ->
            BooleanArray(dimensions.cols) { c ->
                resultChannels[r, c].receive()
            }
        }
        gridChannel.send(grid) // emit aggregated liveness matrix
        endOfFrame()
        return grid
    }

    fun calculateFrameBlocking(): Array<BooleanArray> =
        runBlocking(coroutineContext) {
            launch { calculateFrame() }
            gridChannel.receive()
        }

    private suspend fun endOfFrame() {
        if (period > 0) delay(period)
        if (logRate) {
            framesCount++
            if (System.nanoTime() - lastStatsDump >= 1_000_000_000) {
                System.out.printf("Frames per second: %d\n", framesCount)
                lastStatsDump = System.nanoTime()
                framesCount = 0
            }
        }
    }

    companion object {

        @JvmStatic
        fun create(args: ExecutionArgs): GameOfLife {
            val original: Array<BooleanArray> = PatternParser.parseFile(args.patternFile())
            val rotated = if (args.rotate()) PatternParser.rotate(original) else original
            val pattern = PatternParser.pad(
                rotated,
                args.leftPadding(),
                args.topPadding(),
                args.rightPadding(),
                args.bottomPadding()
            )
            val gridChannel = Channel<Array<BooleanArray>>() // channel carries aggregated liveness matrices
            val dimensions = Dimensions(pattern.size, pattern[0].size, args.toroidal())
            return create(args, dimensions, pattern, gridChannel)
        }

        private fun create(
            args: ExecutionArgs,
            dimensions: Dimensions,
            seed: Array<BooleanArray>,
            gridChannel: Channel<Array<BooleanArray>>
        ): GameOfLife =
            GameOfLife(
                dimensions,
                seed,
                args.periodMilliseconds(),
                gridChannel,
                args.logRate(),
                args.dispatcherType.dispatcher
            )
    }
}
