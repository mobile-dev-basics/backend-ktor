package com.mobile.plugins

import com.mobile.models.Todos
import com.mobile.models.Users
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*


object DatabaseFactory{
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            create(Users)
            create(Todos)
        }
    }

     suspend fun <T> dbQuery(block: suspend () -> T) : T = newSuspendedTransaction(Dispatchers.IO) {block()}
}

