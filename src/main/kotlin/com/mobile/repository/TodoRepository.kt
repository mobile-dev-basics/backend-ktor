package com.mobile.repository

import com.mobile.models.Todo
import com.mobile.models.Todos
import com.mobile.models.Type
import com.mobile.plugins.DatabaseFactory.dbQuery
import com.mobile.services.UserService
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

interface TodoRepository {
    suspend fun getAll(userId: Long) : MutableList<Todo>
    suspend fun findTodo(id: Long) : Todo?
    suspend fun addTodo(name: String, userId: Long, creationDate: LocalDate, endDate: LocalDate?, description: String?, priority: Int?) : Todo?

    suspend fun updateTodo(id: Long, name: String?, priority: Int?, description: String?, endDate: LocalDate?) : Boolean

    suspend fun deleteTodo(id:Long) : Boolean
}

class TodoRepositoryImpl : TodoRepository, KoinComponent{
    private val userService by inject<UserService>()

    private fun resultRowToTodo(row: ResultRow) : Todo {
        val todo = runBlocking {userService.findById(row[Todos.userId])}?.let {
            Todo(id = row[Todos.id],
                name = row[Todos.name],
                creationDate = row[Todos.creationDate],
                user = it
            )
        }
        if (todo != null) {
            todo.description = row[Todos.description]
            todo.endDate = row[Todos.endDate]
            return todo
        }
        error("User was not found")
    }

    override suspend fun getAll(userId : Long): MutableList<Todo> {
        return dbQuery { Todos.select{Todos.userId eq userId}
            .map(::resultRowToTodo)
            .toMutableList()}
    }

    override suspend fun addTodo(name: String, userId: Long, creationDate: LocalDate, endDate: LocalDate?, description: String?, priority: Int?) : Todo?{
        var type: Type = Type.GLOBAL
        if (endDate != null) type = Type.NORMAL
        return dbQuery{
            val insertStatement = Todos.insert {
                it[Todos.name] = name
                it[Todos.priority] = priority ?: 2
                it[Todos.creationDate] = creationDate
                it[Todos.description] = description
                it[Todos.endDate] = endDate
                it[Todos.userId] = userId
            }
           insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToTodo )
        }
    }

    override suspend fun findTodo(id: Long): Todo? = dbQuery {
            Todos
                .select{Todos.id eq id}
                .map(::resultRowToTodo)
                .singleOrNull()
    }


    override suspend fun updateTodo(id: Long, name: String?, priority: Int?, description: String?, endDate: LocalDate?) =
        dbQuery {
            Todos.update({ Todos.id eq id}){
                if(name != null) it[Todos.name] = name
                if(priority != null) it[Todos.priority] = priority
                if(description != null) it[Todos.description] = description
                if(endDate != null) it[Todos.endDate] = endDate
            } > 0
        }

    override suspend fun deleteTodo(id:Long) : Boolean = dbQuery {
        Todos.deleteWhere { Todos.id eq id } > 0
    }



}

