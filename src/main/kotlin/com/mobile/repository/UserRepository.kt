package com.mobile.repository

import com.mobile.models.User
import com.mobile.models.Users
import com.mobile.plugins.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.component.KoinComponent

interface UserRepository{
    suspend fun getAll() : List<User>
    suspend fun getUserById(id: Long) : User?

    suspend fun getUserByEmail(email: String) : User?
    suspend fun addUser(name: String, email: String, password: String, salt: String) : User?

    suspend fun updateUser(id: Long, name:String, email:String, password: String) : Boolean

    suspend fun deleteUser(id: Long) : Boolean

    suspend fun login(email: String, password: String) : User?
}

class UserRepositoryImpl : UserRepository, KoinComponent{

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id], name = row[Users.name], email = row[Users.email], password = row[Users.password], salt = row[Users.salt]
        )
    }

    override suspend fun getAll(): List<User> {
       return dbQuery { Users.selectAll().map(::resultRowToUser) }
    }

    override suspend fun login(email: String, password: String) : User?{
        return dbQuery { Users
            .select((Users.email eq email) and (Users.password eq password))
            .map(::resultRowToUser)
            .singleOrNull()}
    }

    override suspend fun getUserById(id: Long): User? {
        return dbQuery{
            Users
            .select(Users.id eq id)
            .map(::resultRowToUser)
            .singleOrNull()
        }
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users
            .select(Users.email eq email)
            .map (::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addUser(name: String, email: String, password: String, salt:String) =
       dbQuery {
           val insertStatement = Users.insert{
               it[Users.name] = name
               it[Users.email] = email
               it[Users.password] = password
               it[Users.salt] = salt
           }
           insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
       }

    override suspend fun updateUser(id: Long, name:String, email:String, password: String) =
        dbQuery {
            Users.update ({ Users.id eq id}){
                it[Users.name] = name
                it[Users.email] = email
                it[Users.password] = password
            } > 0
        }

    override suspend fun deleteUser(id: Long): Boolean =
        dbQuery {
            Users.deleteWhere { Users.id eq id } > 0
        }


}