package com.mehmetbaloglu.geminichatbot.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetbaloglu.geminichatbot.R
import com.mehmetbaloglu.geminichatbot.model.DataOrException
import com.mehmetbaloglu.geminichatbot.model.MessageModel
import com.mehmetbaloglu.geminichatbot.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val context: Context
) : ViewModel() {

    private val _messageList = MutableStateFlow<List<MessageModel>>(emptyList())
    val messageList: StateFlow<List<MessageModel>> = _messageList

    private val _uiState = MutableStateFlow(DataOrException<List<MessageModel>, Boolean, Exception>())
    val uiState: StateFlow<DataOrException<List<MessageModel>, Boolean, Exception>> = _uiState

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun sendMessage(question: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = DataOrException(data = _messageList.value, loading = true)

        val updatedMessages = _messageList.value + MessageModel(role = "user", message = question)
        _messageList.value = updatedMessages

        val response = repository.sendMessage(question, updatedMessages)
        when {
            response.e != null -> {
                val errorMsg = response.e?.message ?: context.getString(R.string.error_unknown)

                val serverBusyMessage = if (errorMsg.contains("503") || errorMsg.contains("overloaded", true)) {
                    context.getString(R.string.error_server_busy)
                } else {
                    errorMsg
                }

                _errorMessage.value = serverBusyMessage
                _uiState.value = DataOrException(data = updatedMessages, loading = false)

                delay(1500)
                clearErrorMessage()
            }
            response.data != null -> {
                val botMessage = MessageModel(role = "model", message = response.data!!)
                val newMessages = updatedMessages + botMessage
                _messageList.value = newMessages
                _uiState.value = DataOrException(data = newMessages, loading = false, e = null)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    fun clearChatHistory() {
        _messageList.value = emptyList()
        _uiState.value = DataOrException(data = emptyList(), loading = false, e = null)
    }
}
