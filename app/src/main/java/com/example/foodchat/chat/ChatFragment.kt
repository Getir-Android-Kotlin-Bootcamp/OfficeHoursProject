package com.example.foodchat.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodchat.R
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.example.foodchat.chat.ChatMessage
import com.example.foodchat.databinding.FragmentChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)

        // Instantiate gemini model
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyAcOvXxgH1_BRVGYVVjpT1gWYFDHGKTNeU"
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupRecyclerView()

        CoroutineScope(Dispatchers.Main).launch {
            chat = generativeModel.startChat(
                history = listOf(
                    content(role = "user") {
                        text(getString(R.string.gemini_role))
                    },
                    content(role = "model") { text("Hi, how can I help you?") }
                )
            )
        }

        val chatStart = ChatMessage(
            message = "How can I help you?",
            isMessageFromUser = false,
            isLoading = false
        )

        updateRecyclerAdapter(chatStart)

        with(binding) {

            ivDeliver.setOnClickListener {

                val message = etMessage.text.toString().trim()

                if (message.isNotEmpty()) {

                    val chatMessage =
                        ChatMessage(message = message, isMessageFromUser = true, isLoading = false)
                    updateRecyclerAdapter(chatMessage)

                    // Clear EditText
                    binding.etMessage.text?.clear()

                    // Close the keyboard after the message sent
                    val inputMethodManager =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.etMessage.windowToken, 0)

                    val load = chatMessage.copy(isLoading = true)
                    chatAdapter.chatMessages.add(load)

                    val lastMessageFromSender = chatAdapter.chatMessages.last().message

                    getResponseFromGemini(lastMessageFromSender)

                }
            }
        }
        return binding.root

    }

    private fun getResponseFromGemini(lastMessageFromSender: String?) {
        CoroutineScope(Dispatchers.Main).launch {

            lastMessageFromSender?.let {
                val response = async { sendMessage(lastMessageFromSender) }.await()

                val responseChatMessage =
                    ChatMessage(
                        message = response,
                        isMessageFromUser = false,
                        isLoading = false
                    )

                updateRecyclerAdapter(responseChatMessage)
                removeLoadingItem()

            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvChat.layoutManager = LinearLayoutManager(requireContext())
            chatAdapter = ChatAdapter()
            binding.rvChat.adapter = chatAdapter
        }
    }

    private suspend fun sendMessage(message: String): String {
        return chat.sendMessage(message).text.toString()
    }

    private fun updateRecyclerAdapter(chatMessage: ChatMessage) {
        chatAdapter.apply {
            chatMessages.add(chatMessage)
            notifyDataSetChanged()
        }
    }

    private fun removeLoadingItem() {
        chatAdapter.apply {
            chatMessages.removeAt(chatAdapter.chatMessages.lastIndex - 1)
            notifyDataSetChanged()
        }
    }

    private fun initClick() {
        val ivDeliver: ImageView? = view?.findViewById(R.id.ivDeliver)
        ivDeliver?.setOnClickListener {
            Log.d("Chat Fragment", "onCreateView: Tıkladı")
        }
        val ivAttachment = view?.findViewById<ImageView>(R.id.ivAttachment)
        ivAttachment?.setOnClickListener {
            Log.d("Chat Fragment", "onCreateView: Tıkladı")
        }
    }


}