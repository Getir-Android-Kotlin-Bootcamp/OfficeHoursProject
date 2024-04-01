package com.example.foodchat.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.databinding.ItemMessagesBinding
import com.ns.animationtest.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessagesViewHolder>() {

    val chatMessages = mutableListOf<ChatMessage>()

    class MessagesViewHolder(val binding: ItemMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder =
        MessagesViewHolder(
            ItemMessagesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = chatMessages[position].message
        holder.binding.textView.text = message
    }

    override fun getItemCount(): Int = chatMessages.size
}