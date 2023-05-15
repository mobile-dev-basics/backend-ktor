package com.mobile.services

import com.mobile.models.User
import com.mobile.repository.UserRepository

class UserService (private val userRepository: UserRepository){

    suspend fun findById(id: Long): User? {
        return userRepository.getUserById(id)
    }

    suspend fun login(email: String, password: String): User?{
        return userRepository.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String, salt: String): User? {
        return userRepository.addUser(name, email, password, salt)
    }

    suspend fun updateUser(id: Long, name: String, email: String, password: String): Boolean{
        return userRepository.updateUser(id, name, email, password)
    }

    suspend fun getUserByEmail(email: String) : User?{
        return userRepository.getUserByEmail(email)
    }

    suspend fun deleteUser(id: Long): Boolean{
        return userRepository.deleteUser(id)
    }
}