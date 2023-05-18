package com.mobile.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


data class Todo(
    val id: Long,
    var name: String,
    var user: User,
    var description: String,
    var creationDate: LocalDate,
    var endDate: LocalDate,
    var priority: Int = 2,
    var type: Type = Type.GLOBAL,
)

object Todos : Table(){
    val id = long("id").autoIncrement()
    val name = text("name")
    val creationDate = date("creation_date")
    val priority = integer("priority")
    val type = enumerationByName<Type>("type", 50)
    val description = text("description")
    val endDate = date("end_date")
    val userId = long("user_id").references(Users.id)

    override val primaryKey = PrimaryKey(id)
}
