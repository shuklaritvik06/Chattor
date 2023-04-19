package com.example.dto

@kotlinx.serialization.Serializable
data class Message(val message: String, val sender: String, val timestamp: String)
