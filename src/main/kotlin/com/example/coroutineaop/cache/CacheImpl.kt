package com.example.coroutineaop.cache

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CacheImpl: Cache<Any> {
    private val store = ConcurrentHashMap<String, Any>()

    override fun get(key: String): Any? {
        return store[key]
    }

    override fun put(key: String, value: Any) {
        store[key] = value
    }
}