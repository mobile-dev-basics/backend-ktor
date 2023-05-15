package com.mobile.services

import com.mobile.models.Todo
import com.mobile.models.User
import com.mobile.repository.TodoRepository
import javafx.scene.layout.Priority
import java.time.LocalDate

class TodoService(private val todoRepository: TodoRepository) {

    suspend fun getAllTodo(userId: Long): MutableList<Todo>{
        return todoRepository.getAll(userId)
    }

    suspend fun getTodoById(id: Long) : Todo{
        return todoRepository.findTodo(id) ?: error("Can't find todo with id: $id")
    }

    suspend fun addTodo(name: String, userId: Long, creationDate: LocalDate): Todo? {
        return todoRepository.addTodo(name, userId, creationDate, null, null, null)
    }

    suspend fun updateTodo(id: Long, name: String?, endDate: LocalDate?, description: String?, priority: Int?): Boolean {
        return todoRepository.updateTodo(id, name, priority, description, endDate)
    }

    suspend fun deleteTodo(id: Long): Boolean {
        return todoRepository.deleteTodo(id)
    }

}