package com.mehmetbaloglu.geminichatbot.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

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
                val errorMsg = response.e?.message ?: "Bilinmeyen bir hata oluştu"

                // API hatasını kontrol et
                val serverBusyMessage = if (errorMsg.contains("503") || errorMsg.contains("overloaded", true)) {
                    "Sunucu yoğun, birazdan tekrar deneyin"
                } else {
                    errorMsg
                }

                _errorMessage.value = serverBusyMessage // Hata mesajını ayarla

                delay(1500) // 1 saniye bekle
                clearErrorMessage() // Hata mesajını temizle
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