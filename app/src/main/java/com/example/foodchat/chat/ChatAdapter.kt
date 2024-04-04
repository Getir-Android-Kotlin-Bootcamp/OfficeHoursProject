package com.example.foodchat.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.foodchat.R
import com.example.foodchat.databinding.ItemMessagesBinding

class ChatAdapter(val isRes: MutableLiveData<Boolean>) :
    RecyclerView.Adapter<ChatAdapter.MessagesViewHolder>() {

    val chatMessages = mutableListOf<ChatMessage>()


    class MessagesViewHolder(val binding: ItemMessagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder =
        MessagesViewHolder(
            ItemMessagesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = chatMessages[position]
        val lastMessage = chatMessages.last()
        holder.binding.usertextView1.text = message.userInput
        holder.binding.textView1.text = message.message


        if (isRes.value != null) {
            if (isRes.value == true) {
                holder.binding.loadingBar.visibility = View.GONE
                holder.binding.textView.visibility = View.VISIBLE
            }
        } else {
            holder.binding.textView.visibility = View.GONE
        }

        // val gravity = if (message.isMessageFromUser == true) Gravity.END else Gravity.START
        // holder.binding.textView.gravity = gravity


        startAnimations(
            holder.binding.imageView,
            holder.binding.iv2,
            holder.binding.iv3,
            holder.binding.iv4
        )

    }

    override fun getItemCount(): Int = chatMessages.size

    private fun startAnimations(
        imageView: ImageView,
        iv2: ImageView,
        iv3: ImageView,
        iv4: ImageView
    ) {
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