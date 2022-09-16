package gameoflife

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

private val virtualThreadDispatcher =
    Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

val Dispatchers.virtualThread get() = virtualThreadDispatcher
