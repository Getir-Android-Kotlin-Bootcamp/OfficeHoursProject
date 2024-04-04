package com.example.foodchat.chat

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.example.foodchat.databinding.FragmentChatBinding
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        generativeModel = GenerativeModel(
            modelName = getString(R.string.model_name),
            apiKey = getString(R.string.api_key)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAi()
        initRV()

        val initialMessage = getString(R.string.hi_message)
        val initialChatMessage = ChatMessage(message = initialMessage, isMessageFromUser = false)
        addMessage(initialChatMessage)

        with(binding) {
            btSend.setOnClickListener {
                val message = etText.text.toString().trim()


                val chatMessage =
                    ChatMessage(message = message, isMessageFromUser = true, isLoading = false)
                addMessage(chatMessage)


                val load = chatMessage.copy(isLoading = true)
                addMessage(load)
                CoroutineScope(Dispatchers.Main).launch {

                    val lastMessageFromSender = chatAdapter.chatMessages.last().message

                    getResponseFromGemini(lastMessageFromSender)


                    scrollRecyclerViewToBottom(recyclerView)
                }

                etText.text.clear()
                // klavyeyi indiriyor
                val inputMethodManager =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etText.windowToken, 0)

                scrollRecyclerViewToBottom(recyclerView)
            }


            // check etText position
            etText.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (oldTop > top) {
                    scrollRecyclerViewToBottom(recyclerView)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initAi() {
        CoroutineScope(Dispatchers.Main).launch {
            chat = generativeModel.startChat(
                history = listOf(
                    content(role = getString(R.string.user)) {
                        text(
                            getString(R.string.introduce)
                        )
                    },
                    content(role = getString(R.string.model)) { text(getString(R.string.first_data)) }
                )
            )
        }

    }

    private fun initRV() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatAdapter = ChatAdapter()
        binding.recyclerView.adapter = chatAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessage(chatMessage: ChatMessage) {
        chatAdapter.chatMessages.add(chatMessage)
        chatAdapter.notifyDataSetChanged()
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
                addMessage(responseChatMessage)

                removeLoadingItem()

                scrollRecyclerViewToBottom(binding.recyclerView)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeLoadingItem() {
        chatAdapter.apply {
            chatMessages.removeAt(chatAdapter.chatMessages.lastIndex - 1)
            notifyDataSetChanged()
        }
    }

    private fun scrollRecyclerViewToBottom(recyclerView: RecyclerView) {
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

}