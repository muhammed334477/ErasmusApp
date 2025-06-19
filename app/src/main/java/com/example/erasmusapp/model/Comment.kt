package com.example.erasmusapp.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Comment(
    val user: String = "",
    val comment: String = "",
    val rating: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)