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
import com.example.foodchat.databinding.ItemLoadingBinding
import com.example.foodchat.databinding.ItemMessagesBinding
import com.example.foodchat.databinding.ItemMessagesUserBinding

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
        fun bind(message: ChatMessage, position: Int) {
            binding.textView.text = message.message

            val isLastItem = position == itemCount - 1

            if (isLastItem) {
                ObjectAnimator.ofFloat(binding.layoutText, "translationX", -100f, 50f).apply {
                    duration = 500
                    interpolator = OvershootInterpolator()
                    start()
                }
            }
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
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            USER_MESSAGE_TYPE -> {
                val binding = ItemMessagesUserBinding.inflate(inflater, parent, false)
                UserMessagesViewHolder(binding)
            }

            BOT_MESSAGE_TYPE -> {
                val binding = ItemMessagesBinding.inflate(inflater, parent, false)
                BotMessagesViewHolder(binding)
            }

            else -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatMessages[position]
        when (holder) {
            is UserMessagesViewHolder -> holder.bind(message)
            is BotMessagesViewHolder -> holder.bind(message, position)
            is LoadingViewHolder -> holder.setLoading(message)
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    companion object {
        private val USER_MESSAGE_TYPE = R.layout.item_messages_user
        private val BOT_MESSAGE_TYPE = R.layout.item_messages
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatMessages[position]
        return when {
            message.isLoading -> R.layout.item_loading
            message.isMessageFromUser -> R.layout.item_messages_user
            else -> R.layout.item_messages
        }
    }
}