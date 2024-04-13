package com.example.coroutineaop.repository

import com.example.coroutineaop.model.User

interface UserRepository {

    suspend fun getUserById(id: Long): User?
    suspend fun saveUser(user: User): User
}