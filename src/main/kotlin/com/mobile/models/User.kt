package com.mobile.models

import org.jetbrains.exposed.sql.Table

data class User(val id: Long,
                var name: String,
                var email: String,
                var password: String,
                var salt: String
)

object Users : Table(){
    val id = long("id").autoIncrement()
    val name = text("name")
    val email = text("email")
    val password = text("password")
    val salt = text("salt")

    override val primaryKey = PrimaryKey(id)
}

