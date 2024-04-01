package com.example.foodchat.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.ns.animationtest.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private lateinit var chat: Chat
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chatAdapter: ChatAdapter

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

        // Instantiate ChatAdapter
        chatAdapter = ChatAdapter()
        recyclerView.adapter = chatAdapter

        btSend.setOnClickListener {
            val message = etText.text.toString().trim()

            val chatMessage =
                ChatMessage(message = message, isMessageFromUser = true)
            chatAdapter.chatMessages.add(chatMessage)
            chatAdapter.notifyDataSetChanged()

            CoroutineScope(Dispatchers.Main).launch {
                val response = chat.sendMessage(message).text.toString()
                Log.d("TAG", "onCreate: $response")
                chatAdapter.chatMessages.add(
                    ChatMessage(
                        message = response,
                        isMessageFromUser = false
                    )
                )
                chatAdapter.notifyDataSetChanged()
            }
        }

        val imageView = requireView().findViewById<ImageView>(R.id.imageView)
        val iv2 = requireView().findViewById<ImageView>(R.id.iv2)
        val iv3 = requireView().findViewById<ImageView>(R.id.iv3)
        val iv4 = requireView().findViewById<ImageView>(R.id.iv4)


        val anim1 = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -5f).apply {
            duration = 100
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.RESTART
        }

        val anim2 = ObjectAnimator.ofFloat(iv2, "translationY", 0f, -5f).apply {
            duration = 100
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.RESTART
        }

        val anim3 = ObjectAnimator.ofFloat(iv3, "translationY", 0f, -5f).apply {
            duration = 100
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.RESTART
        }
        val anim4 = ObjectAnimator.ofFloat(iv4, "translationY", 0f, -5f).apply {
            duration = 100
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.RESTART
        }


        anim1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                anim2.start()

            }
        })

        anim2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                anim3.start()
            }
        })

        anim3.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                anim4.start()
            }
        })
        anim4.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                anim1.start()
            }
        })

        anim1.start()

    }


}