package com.example.coroutineaop.repository

import com.example.coroutineaop.model.User
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class UserRepositoryImpl: UserRepository {
    private val users = ConcurrentHashMap<Long, User>()

    override suspend fun getUserById(id: Long): User? {
        return users[id]
    }

    override suspend fun saveUser(user: User): User {
        users[user.id] = user
        return user
    }


}