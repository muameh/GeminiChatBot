package com.mehmetbaloglu.geminichatbot.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mehmetbaloglu.geminichatbot.model.DataOrException
import com.mehmetbaloglu.geminichatbot.model.MessageModel
import com.mehmetbaloglu.geminichatbot.ui.viewmodel.MainViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by mainViewModel.uiState.collectAsState()
    val messageList by mainViewModel.messageList.collectAsState()
    val errorMessage by mainViewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val showDialog = remember { mutableStateOf(false) } // Alert için state

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Long)
            mainViewModel.clearErrorMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gemini Chat Bot",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF075E54)),
                actions = {
                    IconButton(onClick = {showDialog.value = true}) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "New Chat",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFECE5DD),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures { keyboardController?.hide() } },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = messageList,
                uiState = uiState
            )
            // Eğer yükleniyorsa "Typing Indicator" ekle
            if (uiState.loading == true) {

                Box(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    TypingIndicator()
                }

            }
            MessageInputField(
                onMessageSend = { mainViewModel.sendMessage(it) }
            )
        }
    }
    // Alert Dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Yeni Sohbet Başlat") },
            text = { Text("Mevcut sohbeti silip yeni bir sohbet başlatmak istiyor musunuz?") },
            confirmButton = {
                TextButton(onClick = {
                    mainViewModel.clearChatHistory()
                    showDialog.value = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Hayır")
                }
            }
        )
    }
}

@Composable
fun MessageInputField(onMessageSend: (String) -> Unit) {
    val message = remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message.value,
            onValueChange = { message.value = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Mesaj yaz...") },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (message.value.isNotBlank()) {
                    onMessageSend(message.value)
                    message.value = ""
                }
            },
            modifier = Modifier.background(Color(0xFF25D366), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier,
    messageList: List<MessageModel>,
    uiState: DataOrException<List<MessageModel>, Boolean, Exception>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        reverseLayout = true
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
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isUser) Color(0xFFDCF8C6) else Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            MarkdownText(
                markdown = message.message,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val dotCount = 3
    val infiniteTransition = rememberInfiniteTransition()

    val dotAlphas = List(dotCount) { _ ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Dot Alpha"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotAlphas.forEachIndexed { index, alpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.Gray.copy(alpha = alpha.value), CircleShape)
            )
        }
    }
}





