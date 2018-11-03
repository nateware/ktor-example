import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import models.TasksDao


fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, module = Application::mainModule).start(wait = true)
}

val tasksDao = TasksDao()

fun Application.mainModule() {

    tasksDao.init()

    install(ContentNegotiation) { jackson() }
    install(StatusPages)
    install(CallLogging)
    install(Locations)

    routing {
        taskRoutes()
        projectRoutes()
    }
}

fun Routing.taskRoutes() {
    @Location("/tasks")
    class GetTasks

    get<GetTasks> {
        val list = tasksDao.all()
        call.respond(list)
    }

    @Location("/tasks/{id}")
    data class GetTask(val id: Int)

    get<GetTask> {
        val task = tasksDao.findById(it.id)
        call.respond(task)
    }
}

fun Routing.projectRoutes() {
    get("/projects") {
        call.respondText("All Projects")
    }

}