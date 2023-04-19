package com.example.plugins

import com.example.chat.chatRoutes
import com.example.rooms.roomRoutes
import com.example.users.userRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    roomRoutes()
    userRoutes()
    routing {
        get("/") {
                call.respond(mapOf("message" to "API IS UP!"))
            }
    }
}
