package gameoflife.ui

import gameoflife.ExecutionArgs
import gameoflife.GameOfLife
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

class WindowOutput private constructor(private val gameOfLife: GameOfLife, args: ExecutionArgs) {
    private val width: Int
    private val height: Int
    private val canvas: Canvas

    @Volatile
    private var cells: Array<BooleanArray> = emptyArray()

    init {
        val (rows, cols) = gameOfLife.dimensions
        val scale = calculateScale(rows, cols, args.maxWindowWidth(), args.maxWindowHeight())
        width = (scale * cols).toInt()
        height = (scale * rows).toInt()
        canvas = Canvas()
        val frame = JFrame("Conway's Game of Life")
        frame.add(canvas)
        frame.setSize(width, height)
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    internal inner class Canvas : JPanel() {
        override fun paint(g: Graphics) {
            super.paint(g)
            if (cells == null) return
            g.color = Color.WHITE
            g.fillRect(0, 0, width, height)
            g.color = Color.LIGHT_GRAY
            val cellWidth = width / cells!![0].size
            run {
                var x = 0
                while (x <= width) {
                    g.drawLine(x, 0, x, height)
                    x += cellWidth
                }
            }
            val cellHeight = height / cells!!.size
            run {
                var y = 0
                while (y <= height) {
                    g.drawLine(0, y, width, y)
                    y += cellHeight
                }
            }
            g.color = Color.BLACK
            for (r in cells!!.indices) {
                for (c in cells!![r].indices) {
                    if (cells!![r][c]) {
                        g.fillRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight)
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun runUI(args: ExecutionArgs, gameOfLife: GameOfLife) {
            val windowOutput = WindowOutput(gameOfLife, args)
            runBlocking {
            while (true) {
                windowOutput.cells = gameOfLife.gridChannel.receive()
                windowOutput.canvas.repaint()
            }
            }
        }

        private fun calculateScale(rows: Int, cols: Int, maxWindowWidth: Int, maxWindowHeight: Int): Double {
            val aspect = maxWindowWidth.toDouble() / maxWindowHeight
            val actual = cols.toDouble() / rows
            return if (actual < aspect) maxWindowHeight.toDouble() / rows else maxWindowWidth.toDouble() / cols
        }
    }
}