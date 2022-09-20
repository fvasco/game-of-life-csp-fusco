package gameoflife

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

class VirtualThreadCoroutineDispatcher(val immediate: Boolean = false) : CoroutineDispatcher() {

    override fun isDispatchNeeded(context: CoroutineContext): Boolean =
        !(immediate && Thread.currentThread().isVirtual)

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Thread.startVirtualThread(block)
    }
}
