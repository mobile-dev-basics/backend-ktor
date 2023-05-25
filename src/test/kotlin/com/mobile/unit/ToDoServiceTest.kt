package com.mobile.unit

import com.mobile.models.Todo
import com.mobile.models.User
import com.mobile.repository.TodoRepository
import com.mobile.services.TodoService
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDate
import kotlin.test.assertEquals

class ToDoServiceTest {

    @Test
    suspend fun testAddToDo() {
        val todo = Todo(
            1,
            "Test",
            User(1, "Bulat", "test@gmail.com", "test", "3"),
            "Test",
            LocalDate.now(),
            LocalDate.now())
        val todoRepository = Mockito.mock(TodoRepository::class.java)
        Mockito.`when`(todoRepository.findTodo(1)).thenReturn(todo)
        val todoService = TodoService(todoRepository)
        todoService.addTodo(todo.name, todo.user, todo.creationDate, todo.endDate, todo.description, todo.priority)
        val newTodo = todoService.getTodoById(1)
        assertEquals(todo, newTodo)
    }
}
