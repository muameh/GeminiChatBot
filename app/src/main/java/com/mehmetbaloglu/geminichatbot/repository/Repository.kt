package com.mehmetbaloglu.geminichatbot.repository

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mehmetbaloglu.geminichatbot.R
import com.mehmetbaloglu.geminichatbot.model.DataOrException
import com.mehmetbaloglu.geminichatbot.model.MessageModel
import javax.inject.Inject

class Repository @Inject constructor(private val generativeModel: GenerativeModel,private val context: Context) {

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
            val message = response.text ?: context.getString(R.string.error_empty_response)
            DataOrException(data = message, loading = false, e=null)
        } catch (e: Exception) {
            Log.d("Response", "Received response: ${e.message}")
            DataOrException(e = e, loading = false, data = e.message.toString())
        }
    }
}