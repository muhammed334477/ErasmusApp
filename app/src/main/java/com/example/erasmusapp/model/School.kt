package com.example.erasmusapp.model

data class School(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val avgRating: Double = 0.0,
    val ratingCount: Int = 0
)