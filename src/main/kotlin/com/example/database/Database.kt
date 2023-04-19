package com.example.database

import com.mongodb.client.MongoClient
import org.litote.kmongo.KMongo

lateinit var client: MongoClient

fun connect(){
    client = KMongo.createClient("mongodb+srv://ritvik:qo4yW8ffUDuqZtFR@cluster0.aeiaykn.mongodb.net/?retryWrites=true&w=majority")
}
