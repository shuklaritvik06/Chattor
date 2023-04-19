package com.example.utils

import com.example.dto.Message
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Connection(private val session: DefaultWebSocketServerSession, private val username: String){
    suspend fun sendMessage(message: Message){
        this.session.send(Json.encodeToString(message))
    }
}