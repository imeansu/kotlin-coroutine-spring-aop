package com.example.coroutineaop.cache

interface CoroutineCache<T> {
    suspend fun get(key: String): T?
    suspend fun put(key: String, value: T)
}