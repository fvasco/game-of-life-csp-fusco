package gameoflife.domain

data class Dimensions(val rows: Int, val cols: Int, val toroidal: Boolean) {
    inline fun forEachRowCol(consumer: (Int, Int) -> Unit) {
        repeat(rows) {r->
            repeat ( cols) {c->
                consumer(r, c)
            }
        }
    }

    fun forEachNeighbor(row: Int, col: Int, consumer: (Int, Int) -> Unit) {
        for (r in row - 1..row + 1) { // [row-1, row+1]
            for (c in col - 1..col + 1) { // [col-1, col+1]
                if (toroidal) {
                    val r1 = if (r < 0) rows - 1 else if (r == rows) 0 else r
                    val c1 = if (c < 0) cols - 1 else if (c == cols) 0 else c
                    registerConsumer(row, col, consumer, r1, c1)
                } else {
                    if (r >= 0 && r < rows && c >= 0 && c < cols) { // exclude out of bounds
                        registerConsumer(row, col, consumer, r, c)
                    }
                }
            }
        }
    }

    private fun registerConsumer(row: Int, col: Int, consumer: (Int, Int) -> Unit, r: Int, c: Int) {
        if (r != row || c != col) { // exclude self
            consumer(r, c)
        }
    }
}
