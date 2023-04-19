package todoapp.plugins

import todoapp.routes.customerRouting
import todoapp.routes.getOrderRoute
import todoapp.routes.listOrderRoutes
import todoapp.routes.totalizeOrderRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

    }
    routing{
        customerRouting()
        listOrderRoutes()
        getOrderRoute()
        totalizeOrderRoute()
    }
}
