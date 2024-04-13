package com.example.coroutineaop.cache

import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CoroutineCacheImpl: CoroutineCache<Any> {
    private val store = ConcurrentHashMap<String, Any>()

    override suspend fun get(key: String): Any? {
        delay(1)
        return store[key]
    }

    override suspend fun put(key: String, value: Any) {
        delay(1)
        store[key] = value
    }
}