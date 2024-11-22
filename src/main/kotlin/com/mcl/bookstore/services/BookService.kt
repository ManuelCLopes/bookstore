package com.mcl.bookstore.services

import com.mcl.bookstore.domain.BookSummary
import com.mcl.bookstore.domain.BookUpdateRequest
import com.mcl.bookstore.domain.dto.BookSummaryDto
import com.mcl.bookstore.domain.entities.BookEntity

interface BookService {
    fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean>
    fun list(authorId: Long? = null): List<BookEntity>
    fun get(isbn: String): BookEntity?
    fun partialUpdate(isbn: String, bookUpdateRequest: BookUpdateRequest): BookEntity
    fun delete(isbn: String)
}