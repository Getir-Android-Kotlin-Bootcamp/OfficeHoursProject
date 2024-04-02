package com.example.foodchat.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.databinding.ItemMessagesBinding
import com.example.foodchat.databinding.ItemMessagesUserBinding
import com.ns.animationtest.ChatMessage

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val chatMessages = mutableListOf<ChatMessage>()

    inner class UserMessagesViewHolder(private val binding: ItemMessagesUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.textView.text = message.message
        }
    }

    inner class BotMessagesViewHolder(private val binding: ItemMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.textView.text = message.message
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            USER_MESSAGE_TYPE -> {
                val binding = ItemMessagesUserBinding.inflate(inflater, parent, false)
                UserMessagesViewHolder(binding)
            }
            else -> {
                val binding = ItemMessagesBinding.inflate(inflater, parent, false)
                BotMessagesViewHolder(binding)
            }
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatMessages[position]
        when (holder) {
            is UserMessagesViewHolder -> holder.bind(message)
            is BotMessagesViewHolder -> holder.bind(message)
        }
    }
    override fun getItemCount(): Int = chatMessages.size

    companion object {
        private const val USER_MESSAGE_TYPE = 0
        private const val BOT_MESSAGE_TYPE = 1
    }
    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isMessageFromUser) {
            USER_MESSAGE_TYPE
        } else {
            BOT_MESSAGE_TYPE
        }
    }
}