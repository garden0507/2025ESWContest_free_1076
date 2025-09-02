package com.example.smartWardrobeApp.model

data class Person(
    val name: String,
    val topOrBottom: String = "",
    val color: String = "",
    val length: String = "",
    val index: Int = -1,
    val customImagePath: String? = null
)
