package com.example.coroutineaop.service

import com.example.coroutineaop.aop.CoroutineCacheable
import com.example.coroutineaop.model.User
import com.example.coroutineaop.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceWithCoroutineCache(
    private val userRepository: UserRepository
) {

    @CoroutineCacheable(prefix = "user", keyExpression = "#id", ttlSecond = 60)
    suspend fun getUserById(id: Long): User? {
        return userRepository.getUserById(id)
    }

    suspend fun saveUser(user: User): User {
        return userRepository.saveUser(user)
    }
}
