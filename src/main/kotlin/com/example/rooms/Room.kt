package com.example.rooms

import com.example.database.client
import com.example.dto.Room
import com.example.dto.UpdateRoom
import com.example.dto.UpdateUser
import com.example.dto.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue
import java.util.*

fun Application.roomRoutes(){
    routing {
        route("/room"){
            post("/create") {
                val room: Room = call.receive()
                println(room)
                val col = client.getDatabase("rooms").getCollection("room")
                val docs: List<Document> = col.find(Room::roomName.eq(room.roomName)).toList()
                if (docs.isNotEmpty()){
                    call.respond(HttpStatusCode.BadRequest, "Already exist!")
                }
                val doc = Document()
                doc.append("roomName",room.roomName)
                doc.append("roomId", UUID.randomUUID().toString())
                col.insertOne(doc)
                call.respond(mapOf("message" to "Room Created!"))
            }
            delete("/delete") {
                val room = call.receive<Room>()
                val col = client.getDatabase("rooms").getCollection("room")
                col.deleteOne(User::userName.eq(room.roomName))
                call.respond(mapOf("message" to "Deleted!"))
            }
            put("/update") {
                val room = call.receive<UpdateRoom>()
                val col = client.getDatabase("rooms").getCollection("room")
                col.updateOne(User::userName.eq(room.roomName), setValue(User::userName, room.newName))
                call.respond(mapOf("message" to "Updated!"))
            }
        }
    }
}
