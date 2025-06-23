package kr.junhyung.mainframe.core.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement

/**
 * Polymorphic helper class for [CoroutineExceptionHandler].
 */
public abstract class AbstractCoroutineExceptionHandler : AbstractCoroutineContextElement(CoroutineExceptionHandler.Key), CoroutineExceptionHandler