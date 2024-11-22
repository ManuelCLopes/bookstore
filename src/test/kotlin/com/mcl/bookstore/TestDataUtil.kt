package com.mcl.bookstore

import com.mcl.bookstore.domain.AuthorSummary
import com.mcl.bookstore.domain.AuthorUpdateRequest
import com.mcl.bookstore.domain.BookSummary
import com.mcl.bookstore.domain.dto.AuthorDto
import com.mcl.bookstore.domain.dto.AuthorSummaryDto
import com.mcl.bookstore.domain.dto.AuthorUpdateRequestDto
import com.mcl.bookstore.domain.dto.BookSummaryDto
import com.mcl.bookstore.domain.entities.AuthorEntity
import com.mcl.bookstore.domain.entities.BookEntity

const val BOOK_A_ISBN = "978-011-410239-7625"

fun testAuthorDtoA(id: Long? = null) = AuthorDto(
        id = id,
        name = "John Doe",
        age = 30,
        description = "Some description",
        image = "author-image.jpg",
    )

fun testAuthorEntityA(id: Long? = null) = AuthorEntity(
        id = id,
        name = "John Doe",
        age = 30,
        description = "Some description",
        image = "author-image.jpg",
)

fun testAuthorEntityB(id: Long? = null) = AuthorEntity(
        id = id,
        name = "Don Joe",
        age = 65,
        description = "Some other description",
        image = "some-other-image.jpeg"
)

fun testAuthorSummaryDtoA(id: Long) = AuthorSummaryDto(
        id = id,
        name = "John Doe",
        image = "author-image.jpg"
)

fun testAuthorSummaryA(id: Long) = AuthorSummary(
        id = id,
        name = "John Doe",
        image = "author-image.jpg"
)

fun testAuthorUpdateDtoA(id: Long? = null) = AuthorUpdateRequestDto(
        id = 999L,
        name = "John Doe",
        age = 30,
        description = "Some description",
        image = "author-image.jpg"
)

fun testAuthorUpdateA(id: Long? = null) = AuthorUpdateRequest(
        id = 999L,
        name = "John Doe",
        age = 30,
        description = "Some description",
        image = "author-image.jpg"
)

fun testBookEntityA(isbn: String, author: AuthorEntity) = BookEntity(
        isbn = isbn,
        title = "Test Book A",
        description = "A test book",
        image = "book-image.jpg",
        authorEntity = author
)

fun testBookSummaryDto(isbn: String, author: AuthorSummaryDto) = BookSummaryDto(
        isbn = isbn,
        title = "Test Book A",
        description = "A test book",
        image = "book-image.jpg",
        author = author
)

fun testBookSummaryA(isbn: String, author: AuthorSummary) = BookSummary(
        isbn = isbn,
        title = "Test Book A",
        description = "A test book",
        image = "book-image.jpg",
        author = author
)

fun testBookSummaryB(isbn: String, author: AuthorSummary) = BookSummary(
        isbn = isbn,
        title = "Test Book B",
        description = "Another test book",
        image = "book-image-b.jpg",
        author = author
)