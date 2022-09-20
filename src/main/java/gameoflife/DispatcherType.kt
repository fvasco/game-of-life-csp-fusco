package gameoflife

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ForkJoinPool

enum class DispatcherType(val dispatcher: CoroutineDispatcher) {
    DEFAULT(Dispatchers.Default),
    COMMON_POOL(ForkJoinPool.commonPool().asCoroutineDispatcher()),
    VIRTUAL(VirtualThreadCoroutineDispatcher(immediate = false)),
    VIRTUAL_IMMEDIATE(VirtualThreadCoroutineDispatcher(immediate = true))
}
