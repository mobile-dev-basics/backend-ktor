package com.mobile.plugins

import com.mobile.models.Todos
import com.mobile.models.Users
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import org.jetbrains.exposed.sql.vendors.currentDialect


object DatabaseFactory{
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:8888/postgres"
        val user = "postgres"
        val password = "Gungun124"
        val database = Database.connect(jdbcURL, driverClassName, user = user, password = password)
        transaction(database) {
            create(Users)
            create(Todos)
        }
    }

     suspend fun <T> dbQuery(block: suspend () -> T) : T = newSuspendedTransaction(Dispatchers.IO) {block()}
}

