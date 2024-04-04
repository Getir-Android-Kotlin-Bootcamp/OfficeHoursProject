package com.example.foodchat.chat

data class ChatMessage(
    val message: String? = null,
    val isMessageFromUser: Boolean = false,
    val isLoading: Boolean = false
)