package gameoflife.domain

class CellsGroup(private val cells: List<Cell>) {
    suspend fun run() {
        while (true) {
            cells.forEach { it.notifyLiveness() }
            cells.forEach { it.calculateNextState() }
        }
    }
}