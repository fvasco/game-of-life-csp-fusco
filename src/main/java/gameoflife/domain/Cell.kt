package gameoflife.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class Cell(private var alive: Boolean,
           private val tick: ReceiveChannel<Unit>,
           private val resultChannel: SendChannel<Boolean>) {
    private val inChannels = ArrayList<ReceiveChannel<Boolean>>()
    private val outChannels = ArrayList<SendChannel<Boolean>>()

    suspend fun run() {
        while (true) {
            notifyLiveness()
            calculateNextState()
        }
    }

    suspend fun notifyLiveness() {
        tick.receive() // wait for tick stimulus
        outChannels.forEach { it.send(alive) } // announce liveness to neighbors
    }

    suspend fun calculateNextState() {
        val neighbors:Int = inChannels.count { it.receive() } // receive liveness from neighbors
        alive = neighbors == 3 || (alive && neighbors == 2) // calculate next state based on game of life rules
        resultChannel.send(alive) // announce resulting next state
    }

    fun addInChannel(channel: ReceiveChannel<Boolean>) {
        inChannels.add(channel)
    }

    fun addOutChannel(channel: SendChannel<Boolean>) {
        outChannels.add(channel)
    }
}