package com.mobile.services

import com.mobile.models.Todo
import com.mobile.models.User
import com.mobile.repository.TodoRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate


class TodoService(private val todoRepository: TodoRepository) : KoinComponent {


    suspend fun getAllTodo(userId: Long): MutableList<Todo>{
        return todoRepository.getAll(userId)
    }

    suspend fun getTodoById(id: Long) : Todo?{
        return todoRepository.findTodo(id)
    }

    suspend fun addTodo(name: String, user: User, creationDate: LocalDate, endDate: LocalDate, description: String, priority: Int): Todo? {
        return todoRepository.addTodo(name, user.id, creationDate, endDate, description, priority)
    }

    suspend fun updateTodo(id: Long, name: String?, endDate: LocalDate?, description: String?, priority: Int?): Boolean {
        return todoRepository.updateTodo(id, name, priority, description, endDate)
    }

    fun checkIfOwner(todo : Todo, user : User) : Boolean{
        return todo.user == user
    }

    suspend fun deleteTodo(id: Long): Boolean {
        return todoRepository.deleteTodo(id)
    }

}