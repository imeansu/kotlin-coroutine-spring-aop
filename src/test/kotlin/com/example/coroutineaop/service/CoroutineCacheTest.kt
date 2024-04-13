package com.example.coroutineaop.service

import com.example.coroutineaop.cache.Cache
import com.example.coroutineaop.cache.CoroutineCache
import com.example.coroutineaop.model.User
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CoroutineCacheTest {

    @Autowired
    private lateinit var userServiceWithCache: UserServiceWithCache
    @Autowired
    private lateinit var userServiceWithCoroutineCache: UserServiceWithCoroutineCache
    @Autowired
    private lateinit var cache: Cache<Any>
    @Autowired
    private lateinit var coroutineCache: CoroutineCache<Any>

    @Test
    fun `UserServiceWithCache Get`():Unit = runBlocking {
        // given
        val user = User(1L, "test")
        userServiceWithCache.saveUser(user)
        userServiceWithCache.getUserById(1L)

        // when
        val result = userServiceWithCache.getUserById(1L)

        // then
        assertThat(result?.id).isEqualTo(user.id)
        assertThat((cache.get("user:1") as? User)?.id).isEqualTo(user.id)
    }

    @Test
    fun `UserServiceWithCoroutineCache Get`():Unit = runBlocking {
        // given
        val user = User(1L, "test")
        userServiceWithCoroutineCache.saveUser(user)
        userServiceWithCoroutineCache.getUserById(1L)

        // when
        val result = userServiceWithCoroutineCache.getUserById(1L)

        // then
        assertThat(result?.id).isEqualTo(user.id)
        assertThat((coroutineCache.get("user:1") as? User)?.id).isEqualTo(user.id)
    }
}


