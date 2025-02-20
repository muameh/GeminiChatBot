package com.mehmetbaloglu.geminichatbot.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mehmetbaloglu.geminichatbot.model.DataOrException
import com.mehmetbaloglu.geminichatbot.model.MessageModel
import javax.inject.Inject

class Repository @Inject constructor(private val generativeModel: GenerativeModel) {

    suspend fun sendMessage(
        question: String,
        messageList: List<MessageModel>
    ): DataOrException<String, Boolean, Exception> {
        return try {
            val chat = generativeModel.startChat(
                history = messageList.map {
                    content(it.role) {
                        text(it.message)
                    }
                }
            )
            val response = chat.sendMessage(question)
            val message = response.text ?: "Error: Empty response"
            DataOrException(data = message, loading = false)
        } catch (e: Exception) {
            Log.d("Response", "Received response: ${e.message}")
            DataOrException(e = e, loading = false)
        }
    }


}