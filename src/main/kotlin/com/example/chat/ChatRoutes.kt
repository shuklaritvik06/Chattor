package com.example.chat

import com.example.database.client
import com.example.dto.ChatResponse
import com.example.dto.Message
import com.example.utils.Connection
import com.mongodb.client.MongoCollection
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.json
import org.litote.kmongo.setValue
import java.util.UUID

fun Application.chatRoutes(){
    val members: MutableMap<String, MutableMap<String,Connection>> = mutableMapOf()
    lateinit var col: MongoCollection<Document>
    lateinit var message: Message
    routing {
        route("/chat"){
            webSocket("/rooms/{roomName}") {
                val name = call.parameters["roomName"]
                val username: String = call.request.queryParameters["username"]!!
                println(members[name]?.get(username))
                if (members[name]?.get(username)!=null){
                    call.respond(HttpStatusCode.BadRequest, "Username already exist!")
                    return@webSocket
                }
                if (members[name]==null){
                    if (name != null) {
                        members.putIfAbsent(name, mutableMapOf())
                    }
                }
                members[name]?.set(username, Connection(this, username))
                for((k,v) in members.entries){
                    if (k==name){
                        for ((u,c) in v.entries){
                            c.sendMessage(Message("$username joined the Chat","admin",System.currentTimeMillis().toString()))
                        }
                    }
                }
                try {
                    for (frame_obj in incoming) {
                        frame_obj as? Frame.Text ?: continue
                        val receivedText = frame_obj.readText()
                        message = Json.decodeFromString<Message>(receivedText)
                        for ((k, v) in members.entries) {
                            if (k == name) {
                                for ((u, c) in v.entries) {
                                    c.sendMessage(message)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    for((k,v) in members.entries){
                        if (k==name){
                            for ((u,c) in v.entries){
                                c.sendMessage(Message("$username left the Chat","admin",System.currentTimeMillis().toString()))
                            }
                        }
                    }
                    members[name]?.remove(username)
                }
                col = name?.let { client.getDatabase("rooms").getCollection(it) }!!
                val document = Document()
                document.append("message",message)
                document.append("id", UUID.randomUUID().toString())
                col.insertOne(document)
            }
            delete("/delete/{roomName}/{id}") {
                val id = call.parameters["id"]
                val room = call.parameters["roomName"]
                col =  client.getDatabase("rooms").getCollection(room!!)
                col.deleteOne(ChatResponse::id.eq(id))
                call.respond("message" to "Deleted Successfully!")
            }
            put("/update/{roomName}/{id}") {
                val newMessage = call.receive<String>()
                val id = call.parameters["id"]
                val room = call.parameters["roomName"]
                col =  client.getDatabase("rooms").getCollection(room!!)
                col.updateOne(ChatResponse::id.eq(id), setValue(ChatResponse::message, newMessage))
            }
            get("/users/{roomName}") {
                val room = call.parameters["roomName"]
                call.respond(mapOf("users" to members[room]?.keys))
            }
        }
    }
}
