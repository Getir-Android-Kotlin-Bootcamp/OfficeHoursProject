package com.example.foodchat.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.example.foodchat.databinding.FragmentChatBinding
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var dotAnim: LinearLayout
    private lateinit var binding: FragmentChatBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initModel()

        initRV()


        val initialMessage = "Hi, how can I help you?"
        val initialChatMessage = ChatMessage(message = initialMessage, isMessageFromUser = false)
        chatAdapter.chatMessages.add(initialChatMessage)
        chatAdapter.notifyDataSetChanged()

        dotAnim = requireView().findViewById<LinearLayout>(R.id.dot_animation)
        hideLinearLayout(dotAnim)

        with(binding) {
            btSend.setOnClickListener {
                val message = etText.text.toString().trim()


                val chatMessage =
                    ChatMessage(message = message, isMessageFromUser = true, isLoading = false)
                chatAdapter.chatMessages.add(chatMessage)
                chatAdapter.notifyDataSetChanged()


                val load = chatMessage.copy(isLoading = true)
                chatAdapter.chatMessages.add(load)
                CoroutineScope(Dispatchers.Main).launch {

                    val lastMessageFromSender = chatAdapter.chatMessages.last().message

                    getResponseFromGemini(lastMessageFromSender)


                    scrollRecyclerViewToBottom(recyclerView)
                }

                etText.text.clear()
                scrollRecyclerViewToBottom(recyclerView)
            }


        }

        //check etText position
        binding.etText.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (oldTop > top) {
                scrollRecyclerViewToBottom(binding.recyclerView)
            }
        }

    }

    private fun initRV() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            chatAdapter = ChatAdapter()
            binding.recyclerView.adapter = chatAdapter
        }
    }

    private fun initModel() {
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
    }


    private fun getResponseFromGemini(lastMessageFromSender: String?) {
        CoroutineScope(Dispatchers.Main).launch {

            lastMessageFromSender?.let {
                val response =
                    async { chat.sendMessage(lastMessageFromSender).text.toString() }.await()

                val responseChatMessage =
                    ChatMessage(
                        message = response,
                        isMessageFromUser = false,
                        isLoading = false
                    )
                chatAdapter.chatMessages.add(responseChatMessage)
                chatAdapter.notifyDataSetChanged()

//                updateRecyclerAdapter(responseChatMessage)
                removeLoadingItem()

            }
        }
    }

    private fun removeLoadingItem() {
        chatAdapter.apply {
            chatMessages.removeAt(chatAdapter.chatMessages.lastIndex - 1)
            notifyDataSetChanged()
        }
    }

    private fun hideLinearLayout(linearLayout: LinearLayout) {
        linearLayout.visibility = View.INVISIBLE
    }

    private fun scrollRecyclerViewToBottom(recyclerView: RecyclerView) {
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

}