package com.cs407.myapplication.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

data class Message(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {},  // Add this parameter for navigation
    viewModel: ChatViewModel = viewModel()
) {
    // We now read messages from the ViewModel
    val messages = viewModel.messages
    var userInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat with Fred") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Map"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Message Fred...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            // Call the ViewModel to send the message
                            viewModel.sendMessage(userInput)
                            userInput = ""
                        }
                    },
                    enabled = userInput.isNotBlank(),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),

            ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message.text,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}