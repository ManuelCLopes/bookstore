package com.mcl.bookstore.domain.dto

data class BookDto(
    val isbn: String,
    val title: String,
    val description: String,
    val image: String,
    val authorEntity: AuthorDto
)
