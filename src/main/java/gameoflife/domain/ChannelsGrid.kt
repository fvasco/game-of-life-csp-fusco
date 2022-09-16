package gameoflife.domain

import kotlinx.coroutines.channels.Channel

class ChannelsGrid<T>(val dimensions: Dimensions) {

    private val grid: Array<Array<Channel<T>>> =
        Array(dimensions.rows) {
            Array(dimensions.cols) {
                Channel()
            }
        }

    operator fun get(r: Int, c: Int): Channel<T> = grid[r][c]
}