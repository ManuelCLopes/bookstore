package com.mcl.bookstore.domain.dto

data class BookUpdateRequestDto(
    val title: String? = null,
    val description: String? = null,
    val image: String? = null,
)
