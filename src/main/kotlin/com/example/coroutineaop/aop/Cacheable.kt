package com.example.coroutineaop.aop

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Cacheable(
    val prefix: String,
    val keyExpression: String,
    val ttlSecond: Int
)
