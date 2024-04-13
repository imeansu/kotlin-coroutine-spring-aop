package com.example.coroutineaop.aop

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CoroutineCacheable(
    val prefix: String,
    val keyExpression: String,
    val ttlSecond: Int
)
