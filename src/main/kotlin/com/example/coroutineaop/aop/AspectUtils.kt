package com.example.coroutineaop.aop

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.aspectj.lang.ProceedingJoinPoint
import reactor.core.publisher.Mono
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

val ProceedingJoinPoint.coroutineContinuation: Continuation<Any?>
    get() = this.args.last() as Continuation<Any?>

val ProceedingJoinPoint.coroutineArgs: Array<Any?>
    get() = this.args.sliceArray(0 until this.args.size - 1)

suspend fun ProceedingJoinPoint.proceedCoroutine(
    args: Array<Any?> = this.coroutineArgs
): Any? {
    val rtn = suspendCoroutineUninterceptedOrReturn<Any?> { continuation ->
        this.proceed(args + continuation)
    }

    // TODO: flow 일 경우 처리 필요
    if (rtn is Mono<*>) {
        // for spring 6.1.0 and later
        return rtn.awaitSingleOrNull()
    }
    return rtn
}

fun ProceedingJoinPoint.runCoroutine(
    block: suspend () -> Any?
): Mono<*> {
    val continuation = this.coroutineContinuation
    return mono {
        block.startCoroutineUninterceptedOrReturn(continuation)
    }
}