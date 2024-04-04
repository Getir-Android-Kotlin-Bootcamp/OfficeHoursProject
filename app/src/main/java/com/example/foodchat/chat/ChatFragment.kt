package com.example.foodchat.chat

import android.annotation.SuppressLint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        val etText = requireView().findViewById<EditText>(R.id.etText)
        val btSend = requireView().findViewById<ImageButton>(R.id.btSend)

        generativeModel = GenerativeModel(
            modelName = getString(R.string.model_name),
            apiKey = getString(R.string.api_key)
        )

        GlobalScope.launch {
            chat = generativeModel.startChat(
                history = listOf(
                    content(role = getString(R.string.user_role)) {
                        text(

                                    getString(R.string.ai_generator_text)
                        )
                    },
                    content(role = getString(R.string.model_role)) { text(getString(R.string.hi_text)) }
                )
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Instantiate ChatAdapter
        chatAdapter = ChatAdapter()
        recyclerView.adapter = chatAdapter

        val initialMessage = getString(R.string.hi_message)
        val initialChatMessage = ChatMessage(message = initialMessage, isMessageFromUser = false)
        addMsgToAdapter(initialChatMessage)

        dotAnim = requireView().findViewById(R.id.dot_animation)
        hideLinearLayout(dotAnim)

        btSend.setOnClickListener {
            val message = etText.text.toString().trim()


            val chatMessage =
                ChatMessage(message = message, isMessageFromUser = true, isLoading = false)
            addMsgToAdapter(chatMessage)


            val load = chatMessage.copy(isLoading = true)
            addMsgToAdapter(load)
            CoroutineScope(Dispatchers.Main).launch {

                val lastMessageFromSender = chatAdapter.chatMessages.last().message

                getResponseFromGemini(lastMessageFromSender)


                scrollRecyclerViewToBottom(recyclerView)
            }

            etText.text.clear()
            scrollRecyclerViewToBottom(recyclerView)
        }


        //check etText position
        etText.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (oldTop > top) {
                Log.v("SA", "22")
                scrollRecyclerViewToBottom(recyclerView)
            }
        }

    }
@SuppressLint("NotifyDataSetChanged")
private fun addMsgToAdapter(chatMessage: ChatMessage){
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
                addMsgToAdapter(responseChatMessage)

//                updateRecyclerAdapter(responseChatMessage)
                removeLoadingItem()

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

    private fun hideLinearLayout(linearLayout: LinearLayout) {
        linearLayout.visibility = View.INVISIBLE
    }

    private fun scrollRecyclerViewToBottom(recyclerView: RecyclerView) {
        recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

}