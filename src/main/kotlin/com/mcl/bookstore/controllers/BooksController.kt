package com.mcl.bookstore.controllers

import com.mcl.bookstore.domain.dto.BookSummaryDto
import com.mcl.bookstore.domain.dto.BookUpdateRequestDto
import com.mcl.bookstore.exceptions.InvalidAuthorException
import com.mcl.bookstore.services.BookService
import com.mcl.bookstore.toBookSummary
import com.mcl.bookstore.toBookSummaryDto
import com.mcl.bookstore.toBookUpdateRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1/books"])
class BooksController (
    val bookService: BookService
){

    @PutMapping(path = ["/{isbn}"])
    fun createFullUpdateBook(@PathVariable("isbn") isbn: String, @RequestBody book: BookSummaryDto) : ResponseEntity<BookSummaryDto>{
        return try {
            val (savedBook, isCreated) = bookService.createUpdate(isbn, book.toBookSummary())
            val responseCode = if(isCreated) HttpStatus.CREATED else HttpStatus.OK
            ResponseEntity(savedBook.toBookSummaryDto(), responseCode)
        } catch (ex: InvalidAuthorException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (ex: IllegalStateException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun readManyBooks(@RequestParam("author") authorId: Long?): List<BookSummaryDto>{
        return bookService.list(authorId).map { it.toBookSummaryDto() }
    }

    @GetMapping(path = ["/{isbn}"])
    fun readOneBook(@PathVariable("isbn") isbn: String): ResponseEntity<BookSummaryDto>{
        return bookService.get(isbn)?.let { ResponseEntity(it.toBookSummaryDto(), HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PatchMapping(path = ["/{isbn}"])
    fun partialUpdateBook(@PathVariable("isbn") isbn: String,
                          @RequestBody bookUpdateRequestDto: BookUpdateRequestDto
    ): ResponseEntity<BookSummaryDto> {
        return try {
            val updatedBook = bookService.partialUpdate(isbn, bookUpdateRequestDto.toBookUpdateRequest())
            ResponseEntity(updatedBook.toBookSummaryDto(), HttpStatus.OK)
        } catch (ex: IllegalStateException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping(path = ["/{isbn}"])
    fun deleteBook(@PathVariable("isbn") isbn: String): ResponseEntity<Unit> {
        bookService.delete(isbn)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}