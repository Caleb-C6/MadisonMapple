package com.cs407.myapplication.ui.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyAWfQL8_iaQhLBQ5LfeD6BxpcMV1Pub9Ag"
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            // We "fake" the system instruction by pretending the model already agreed to be Fred
            content(role = "user") { text("You are a roommate named Fred. You are looking for a roommate. Develop a random persona with different race, sleep timing, etc. ") },
            content(role = "model") { text("Got it! I'm Fred. Hey, nice to meet you! I'm looking for a chill roommate.") }
        )
    )

    // 3. State to hold messages for the UI
    val messages = mutableStateListOf<Message>()

    init {
        messages.add(Message("Hey there! I'm Fred. I'm looking for a roommate. What's your vibe?", false))
    }


    fun sendMessage(userText: String) {

        messages.add(Message(userText, true))

        viewModelScope.launch {
            try {

                val response = chat.sendMessage(userText)


                response.text?.let { modelResponse ->
                    messages.add(Message(modelResponse, false))
                }
            } catch (e: Exception) {
                messages.add(Message("Error: ${e.localizedMessage}", false))
            }
        }
    }
}