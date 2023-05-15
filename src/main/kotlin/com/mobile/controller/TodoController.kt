package com.mobile.controller

import com.mobile.services.TodoService
import com.mobile.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.todoRouting(){

    val todoService by inject<TodoService>()
    val userService by inject<UserService>()

    route("/api/todo"){
        get{

        }
        delete("/{id}"){
            if (call.parameters["id"] == null){
                call.respondText("Must send id!", status = HttpStatusCode.BadRequest)
            }
            else{
                call.parameters["id"]?.let{ it1 -> todoService.deleteTodo( it1.toLong()) }
            }
        }


    }
}