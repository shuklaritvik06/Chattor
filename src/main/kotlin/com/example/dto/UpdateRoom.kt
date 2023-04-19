package com.example.dto

import kotlinx.serialization.SerialName

data class UpdateRoom(@SerialName("room_name") val roomName: String, val newName: String)
