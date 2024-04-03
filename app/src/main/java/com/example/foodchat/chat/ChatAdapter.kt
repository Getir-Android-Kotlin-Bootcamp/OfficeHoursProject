package com.example.foodchat.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.example.foodchat.databinding.ItemContainerReceivedMessageBinding
import com.example.foodchat.databinding.ItemContainerSentMessageBinding
import com.example.foodchat.databinding.ItemLoadingBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val chatMessages = mutableListOf<ChatMessage>()

    class SentMessageViewHolder(private val binding: ItemContainerSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            binding.tvSenderMessage.text = chatMessage.message
        }
    }

    class ReceivedMessageViewHolder(private val binding: ItemContainerReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(chatMessage: ChatMessage) {
            binding.tvReceiverMessage.text = chatMessage.message
        }
    }

    class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setLoading(message: ChatMessage) {

            with(binding) {
                if (message.isLoading) {
                    linearLayout.visibility = View.VISIBLE
                    setAnimation(binding)
                } else {
                    linearLayout.visibility = View.GONE
                }
            }
        }

        private fun setAnimation(binding: ItemLoadingBinding) {
            binding.apply {

                ObjectAnimator.ofFloat(linearLayout, "translationX", -100f, 50f).apply {
                    duration = 1000
                    interpolator = OvershootInterpolator()
                    start()
                }

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_container_sent_message ->
                SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            R.layout.item_container_received_message -> ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            R.layout.item_loading -> LoadingViewHolder(
                ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            R.layout.item_container_sent_message -> (holder as SentMessageViewHolder).setData(
                chatMessages[position]
            )

            R.layout.item_container_received_message -> (holder as ReceivedMessageViewHolder).setData(
                chatMessages[position]
            )

            R.layout.item_loading -> (holder as LoadingViewHolder).setLoading(
                chatMessages[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatMessages[position]
        return when {
            message.isLoading -> R.layout.item_loading
            message.isMessageFromUser -> R.layout.item_container_sent_message
            else -> R.layout.item_container_received_message
        }
    }
}