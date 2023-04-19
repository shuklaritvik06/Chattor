package com.example.users

import com.example.database.client
import com.example.dto.UpdateUser
import com.example.dto.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.Document
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.util.*

fun Application.userRoutes(){
    routing {
        route("/user"){
            post("/create") {
                val user = call.receive<User>()
                val col = client.getDatabase("users").getCollection("user")
                val doc = Document()
                val docs: List<Document> = col.find(User::userName.eq(user.userName)).toList()
                if (docs.isNotEmpty()){
                    call.respond(HttpStatusCode.BadRequest, "Already exist!")
                }
                doc.append("userName", user.userName)
                doc.append("userId", UUID.randomUUID().toString())
                col.insertOne(doc)
                call.respond(mapOf("message" to "User Created!"))
            }
            delete("/delete") {
                val user = call.receive<User>()
                val col = client.getDatabase("users").getCollection("user")
                col.deleteOne(User::userName.eq(user.userName))
                call.respond(mapOf("message" to "Deleted!"))
            }
            put("/update") {
                val user = call.receive<UpdateUser>()
                val col = client.getDatabase("users").getCollection("user")
                col.updateOne(User::userName.eq(user.userName), setValue(User::userName, user.new))
                call.respond(mapOf("message" to "Updated!"))
            }
        }
    }
}