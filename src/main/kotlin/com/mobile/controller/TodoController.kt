package com.mobile.controller

import ch.qos.logback.classic.Logger
import com.mobile.dto.requests.TodoCreateRequest
import com.mobile.models.Todo
import com.mobile.models.User
import com.mobile.services.TodoService
import com.mobile.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


fun Route.todoRouting(){

    val todoService by inject<TodoService>()
    val userService by inject<UserService>()
    val logger by inject<Logger>()

    deleteTodoById(todoService, userService, logger)
    getTodoList(todoService, userService, logger)
    getTodoById(todoService, userService, logger)
    addTodo(todoService, userService, logger)
    updateTodo(todoService, userService, logger)
}

fun Route.deleteTodoById(todoService: TodoService, userService: UserService, logger: Logger){
    authenticate{
        delete("/api/todo/{id}") {
            val todoId = call.parameters["id"]?.toLong()
            logger.info("Got request to delete todo with id = $todoId")
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
            if (userId == null || todoId == null){
                logger.info("User id = $userId or todoId = $todoId, failed")
                call.respond(HttpStatusCode.BadRequest, "Either authorization is null or todoId is null!")
                return@delete
            }
            val user : User? = userService.findById(userId)
            val todo : Todo? = todoService.getTodoById(todoId)
            if (user == null || todo == null){
                logger.info("User or todo not found! User = $user, todo = $todo")
                call.respond(HttpStatusCode.Conflict, "User or todo is not found!")
                return@delete
            }

            if(!todoService.checkIfOwner(todo, user)) {
                logger.info("Todo $todo do not belong to user $user")
                call.respond(HttpStatusCode.MethodNotAllowed, "Todo is not this user's!")
                return@delete
            }
            call.respond(HttpStatusCode.OK , todoService.deleteTodo(todoId))
            logger.info("Successfully responded to delete request!")
        }
    }
}

fun Route.getTodoById(todoService: TodoService, userService: UserService, logger: Logger){

    authenticate{
        get("/api/todo/{id}"){
            val todoId = call.parameters["id"]?.toLong()
            logger.info("Got request to get todo with id = $todoId")
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
            if (userId == null || todoId == null){
                logger.info("User id = $userId or todoId = $todoId, failed")
                call.respond(HttpStatusCode.BadRequest, "Either authorization is null or todoId is null!")
                return@get
            }
            val user = userService.findById(userId)
            val todo = todoService.getTodoById(todoId)
            if (user == null || todo == null){
                logger.info("User or todo not found! User = $user, todo = $todo")
                call.respond(HttpStatusCode.Conflict, "User or todo is not found")
                return@get
            }

            if (!todoService.checkIfOwner(todo, user)){
                logger.info("Todo $todo do not belong to user $user")
                call.respond(HttpStatusCode.MethodNotAllowed, "Todo is not this user's!")
                return@get
            }
            call.respond(HttpStatusCode.OK, todo)
            logger.info("Successfully responded to get request!")
        }
    }
}

fun Route.getTodoList(todoService: TodoService, userService: UserService, logger: Logger){

    authenticate{
        get("/api/todo") {
            logger.info("Got request to get all todos")
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
            val user : User?
            if (userId != null) {
                user = userService.findById(userId)
            }
            else{
                logger.info("User id = null, failed")
                call.respond(HttpStatusCode.Unauthorized, "No user!")
                return@get
            }
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not found!")
                logger.info("User is not found, failed")
                return@get
            }
            val response = todoService.getAllTodo(userId)
            call.respond(HttpStatusCode.OK, response)
            logger.info("Successfully responded to get request!")
        }
    }
}



fun Route.addTodo(todoService: TodoService, userService: UserService, logger: Logger){

    authenticate{
        route("/api/todo"){
            post{
                logger.info("Got request to add a new Todo")
                val todoDTO = call.receive<TodoCreateRequest>()
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    logger.info("UserId is null, failed")
                    call.respond(HttpStatusCode.BadRequest, "Authorization is null!")
                    return@post
                }

                val user = userService.findById(userId)
                if(user == null){
                    logger.info("User is null, failed")
                    call.respond(HttpStatusCode.BadRequest, "User not found!")
                    return@post
                }
                val priority = when (todoDTO.priority) {
                    "Low" -> 1
                    "Middle" -> 2
                    else -> 3
                }

                val success = todoService.addTodo(todoDTO.title,
                    user,
                    LocalDate.now(),
                    Instant.ofEpochMilli(todoDTO.date).atZone(ZoneId.systemDefault()).toLocalDate(),
                    todoDTO.description,
                    priority)

                if(success == null){
                    logger.info("Database failed to create object")
                    call.respond(HttpStatusCode.InternalServerError, "Server did not create Todo")
                    return@post
                }


                call.respond(HttpStatusCode.OK, success)
                logger.info("Successfully added a new todo!")
            }
        }
    }
}

fun Route.updateTodo(todoService: TodoService, userService: UserService, logger: Logger){

    authenticate{
        put("/api/todo/{id}"){
            val todoId = call.parameters["id"]?.toLong()
            logger.info("Got a request to update todo with id = $todoId")
            val todoDTO = call.receive<TodoCreateRequest>()
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)

            if (todoId == null || userId == null){
                logger.info("TodoId = $todoId or UserId = $userId, failed")
                call.respond(HttpStatusCode.BadRequest, "Authorization or todo id is null!")
                return@put
            }

            val user = userService.findById(userId)
            if(user == null){
                logger.info("User could not be found!, failed")
                call.respond(HttpStatusCode.BadRequest, "User not found!")
                return@put
            }
            val priority = when (todoDTO.priority) {
                "Low" -> 1
                "Middle" -> 2
                else -> 3
            }

            val success = todoService
                .updateTodo(
                    todoId,
                todoDTO.title,
                Instant.ofEpochMilli(todoDTO.date).atZone(ZoneId.systemDefault()).toLocalDate(),
                    todoDTO.description,
                    priority)

            if (!success) {
                logger.info("Database failed to update the todo!")
                call.respond(HttpStatusCode.InternalServerError, "Server has failed to update the todo")
                return@put
            }

            call.respond(HttpStatusCode.OK, "Success!")
            logger.info("Successfully responded to the request!")
        }
    }
}

