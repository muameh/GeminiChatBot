package com.mehmetbaloglu.geminichatbot.ui.screens

import android.text.Html
import android.text.Spanned
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mehmetbaloglu.geminichatbot.model.MessageModel
import com.mehmetbaloglu.geminichatbot.ui.viewmodel.MainViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by mainViewModel.uiState.collectAsState()
    val messages = uiState.data ?: emptyList()
    val messageList by mainViewModel.messageList.collectAsState()

    Scaffold(
        modifier = Modifier.padding(4.dp),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ChatBot", color = Color.Red) // Arka planı ayarla
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { keyboardController?.hide() }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Mesaj listesini görüntüle
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = messageList
            )

            // Mesaj girişi ve gönderme alanı
            MessageInputField(
                onMessageSend = { mainViewModel.sendMessage(it) }
            )

            // Yükleniyor Durumu
            if (uiState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun MessageInputField(onMessageSend: (String) -> Unit) {
    val message = remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .background(color = Color(0xfff1af0e), shape = RoundedCornerShape(8.dp)),
        value = message.value,
        onValueChange = { message.value = it },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (message.value.isNotBlank()) {
                        onMessageSend(message.value)
                        message.value = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        }
    )
}

@Composable
fun MessageList(modifier: Modifier, messageList: List<MessageModel>) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        reverseLayout = true // Mesajlar ters sırayla yazılacak
    ) {
        items(messageList.reversed()) { message ->
            MessageItem(message)
        }
    }
}

@Composable
fun MessageItem(message: MessageModel) {
    val isUser = message.role == "user"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(shape = RoundedCornerShape(8.dp), color = Color.Transparent),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = message.message,
            color = Color.White,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            fontSize = 16.sp,
            modifier = Modifier
                .background(
                    if (isUser) Color(0xFF0ef1af) else Color(0xfff10e50),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )
    }

}







