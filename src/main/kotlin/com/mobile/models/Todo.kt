package com.mobile.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


data class Todo(
    var name: String,
    var creationDate: LocalDate,
    var user: User,
    val id: Long
){
    var priority: Int = 2
    var type: Type = Type.GLOBAL
    var description: String? = null
    var endDate: LocalDate? = null
}

object Todos : Table(){
    val id = long("id").autoIncrement()
    val name = text("name")
    val creationDate = date("creation_date")
    val priority = integer("priority")
    val type = enumerationByName<Type>("type", 50)
    val description = text("description").nullable()
    val endDate = date("end_date").nullable()
    val userId = long("user_id").references(Users.id)

    override val primaryKey = PrimaryKey(id)
}
