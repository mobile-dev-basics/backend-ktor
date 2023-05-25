package com.mobile.unit

import com.mobile.models.Todo
import com.mobile.models.User
import com.mobile.repository.TodoRepository
import com.mobile.repository.UserRepository
import com.mobile.services.TodoService
import com.mobile.services.UserService
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDate
import kotlin.test.assertEquals

class UserServiceTest {
    @Test
    suspend fun testAddToDo() {
        val user = User(1, "Bulat", "test@gmail.com", "test", "3")
        val userRepository = Mockito.mock(UserRepository::class.java)
        Mockito.`when`(userRepository.getUserById(1)).thenReturn(user)
        val userService = UserService(userRepository)
        userService.register(user.name, user.email, user.password, user.salt)
        val newUser = userService.findById(1)
        assertEquals(user, newUser)
    }
}
