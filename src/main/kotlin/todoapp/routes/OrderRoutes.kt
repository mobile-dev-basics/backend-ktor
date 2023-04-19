package todoapp.routes

import todoapp.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listOrderRoutes(){
    get("/order"){
        if(orderStorage.isNotEmpty()){
            call.respond(orderStorage)
        }
    }
}

fun Route.getOrderRoute(){
    get("/order/{id?}"){
        val id = call.parameters["id"] ?: return@get call.respondText ("Bad request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find{it.number == id} ?: return@get call.respondText(
            "Not found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute(){
    get("/order/{id?}/total"){
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find {it.number == id} ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.sumOf { it.price * it.amount }
        call.respond(total)
    }
}


//    route("/order"){
//        get{
//            if (orderStorage.isNotEmpty()){
//                call.respond(orderStorage)
//            }
//            else{
//                call.respondText("No orders found", status = HttpStatusCode.NotFound)
//            }
//        }
//        get("{date?}"){
//            val date = call.parameters["date"] ?: return@get call.respondText(
//                "Missing id",
//                status = HttpStatusCode.BadRequest
//            )
//            val order =  orderStorage.find{it.number == date} ?: return@get call.respondText(
//                "No order from $date",
//                status = HttpStatusCode.BadRequest
//            )
//            call.respond(order)
//        }
//        get("{date?}/total"){
//            val date = call.parameters["date"] ?: return@get call.respondText(
//                "Missing id",
//                status = HttpStatusCode.BadRequest
//            )
//            val order =  orderStorage.find{it.number == date} ?: return@get call.respondText(
//                "No order from $date",
//                status = HttpStatusCode.BadRequest
//            )
//            var result : Double = 0.0
//            for (i in 0 .. order.contents.size){
//                val current = order.contents.get(i)
//                result += current.amount * current.price
//            }
//            call.respond(result)
//        }
//    }
