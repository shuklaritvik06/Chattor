package com.example.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Room(@SerialName("room_name") val roomName: String)
