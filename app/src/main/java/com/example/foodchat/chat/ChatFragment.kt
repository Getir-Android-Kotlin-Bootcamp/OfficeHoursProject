package com.example.foodchat.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodchat.R
import com.example.foodchat.databinding.FragmentChatBinding
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var binding: FragmentChatBinding

    var isRes : MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = getString(R.string.api_key)
        )
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        initAi()


        initRV()

    with(binding){


        btSend.setOnClickListener {


            isRes.value = false
            val message = etText.text.toString().trim()
            etText.text.clear()
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etText.windowToken, 0)

            val chatMessage = ChatMessage(userInput = message)

            chatAdapter.apply {
               chatMessages.add(chatMessage)
               notifyDataSetChanged()


                MainScope().launch {

                    val response = chat.sendMessage(message).text.toString()

                    chatMessages.add(
                        ChatMessage(
                            message = response,
                            userInput = message
                        )
                    )
                    isRes.value = true

                        chatMessages.removeAt(chatMessages.lastIndex - 1)
                        notifyDataSetChanged()



                }
            }

        }
}


    }



    @OptIn(DelicateCoroutinesApi::class)
    private  fun initAi(){

        GlobalScope.launch {
            chat = generativeModel.startChat(
                history = listOf(
                    content(role = getString(R.string.user)) {
                        text(
                            getString(R.string.introduce))
                    },
                    content(role = getString(R.string.model)) { text(getString(R.string.first_data)) }
                )
            )
        }
    }
private fun initRV(){
    binding.  recyclerView.layoutManager = LinearLayoutManager(requireContext())

    chatAdapter = ChatAdapter(isRes)
    binding. recyclerView.adapter = chatAdapter
}
    @SuppressLint("NotifyDataSetChanged")
    private fun addMessage(chatMessage: ChatMessage){
        chatAdapter.chatMessages.add(chatMessage)
        chatAdapter.notifyDataSetChanged()
    }


}