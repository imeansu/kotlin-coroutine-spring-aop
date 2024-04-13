package com.example.coroutineaop.cache

interface Cache<T> {
    fun get(key: String): T?
    fun put(key: String, value: T)
}