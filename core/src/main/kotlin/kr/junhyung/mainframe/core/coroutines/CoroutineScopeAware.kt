package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CoroutineScope

public interface CoroutineScopeAware {

    public fun setCoroutineScope(coroutineScope: CoroutineScope)

}