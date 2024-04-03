package com.example.foodchat.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.ns.animationtest.ChatMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chatAdapter: ChatAdapter
    var isRes : MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        val etText = requireView().findViewById<EditText>(R.id.etText)
        val btSend = requireView().findViewById<Button>(R.id.btSend)



        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyAcOvXxgH1_BRVGYVVjpT1gWYFDHGKTNeU"
        )

        GlobalScope.launch {
            chat = generativeModel.startChat(
                history = listOf(
                    content(role = "user") {
                        text(
                            " \"You are an ai assistant for food courier application. \" +\n" +
                                    "                    \"Customers may ask you some questions about foods, couriers, restaurant etc.\" +\n" +
                                    "                    \"Pretend to an assistant of this application. I will ask you questions as a customer. You can access to everything. \" +\n" +
                                    "                    \"Don't give me examples. Just say okay I will be an ai assistant and how can I help you?\"\n"
                        )
                    },
                    content(role = "model") { text("Hi, how can I help you?") }
                )
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatAdapter = ChatAdapter(isRes)
        recyclerView.adapter = chatAdapter



        btSend.setOnClickListener {
            isRes.value = false
            val message = etText.text.toString().trim()

            val chatMessage = ChatMessage(userInput = message)
            chatAdapter.chatMessages.add(chatMessage)
            chatAdapter.notifyDataSetChanged()

            MainScope().launch {
                val response = chat.sendMessage(message).text.toString()

                chatAdapter.chatMessages.add(
                    ChatMessage(
                        message = response,
                    )
                )
                isRes.value = true
                chatAdapter.notifyDataSetChanged()
            }
        }
    }
}