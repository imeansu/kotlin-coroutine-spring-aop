package com.example.coroutineaop.service

import com.example.coroutineaop.aop.Cacheable
import com.example.coroutineaop.model.User
import com.example.coroutineaop.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceWithCache(
    private val userRepository: UserRepository
) {

    @Cacheable(prefix = "user", keyExpression = "#id", ttlSecond = 60)
    suspend fun getUserById(id: Long): User? {
        return userRepository.getUserById(id)
    }

    suspend fun saveUser(user: User): User {
        return userRepository.saveUser(user)
    }
}
