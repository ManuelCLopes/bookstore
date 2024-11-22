package com.mcl.bookstore.services.impl

import com.mcl.bookstore.*
import com.mcl.bookstore.domain.AuthorSummary
import com.mcl.bookstore.domain.BookUpdateRequest
import com.mcl.bookstore.repositories.AuthorRepository
import com.mcl.bookstore.repositories.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@SpringBootTest
@Transactional
class BookServiceImplTest @Autowired constructor(
    private val underTest: BookServiceImpl,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
){

    @Test
    fun `test that createUpdate throws IllegalStateException when Author does not exist`(){
        val authorSummary = AuthorSummary(id = 999L)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)
        assertThrows<IllegalStateException> {
            underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        }
    }

    @Test
    fun `test that createUpdate successfully creates book in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val authorSummary = AuthorSummary(id = savedAuthor.id!!)
        val bookSummary = testBookSummaryA(BOOK_A_ISBN, authorSummary)
        val (savedBook, isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        assertThat(savedBook).isNotNull()

        val recalledBook = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(recalledBook).isNotNull()
        assertThat(recalledBook).isEqualTo(savedBook)
        assertThat(isCreated).isTrue()
    }

    @Test
    fun `test that createUpdate successfully updates book in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))

        val authorSummary = AuthorSummary(id = savedAuthor.id!!)
        val bookSummary = testBookSummaryB(BOOK_A_ISBN, authorSummary)
        val (updatedBook, isCreated) = underTest.createUpdate(BOOK_A_ISBN, bookSummary)
        assertThat(savedBook).isNotNull()

        val recalledBook = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(recalledBook).isNotNull()
        assertThat(recalledBook).isEqualTo(updatedBook)
        assertThat(isCreated).isFalse()
    }

    @Test
    fun `test that list returns an empty list when no books in the database`(){
        val result = underTest.list()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test that list returns books when books in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val result = underTest.list()
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that list returns no books when the author Id does not match`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val result = underTest.list(authorId = savedAuthor.id!! + 1 )
        assertThat(result).hasSize(0)
    }

    @Test
    fun `test that list returns book when the author Id does match`() {
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val result = underTest.list(authorId = savedAuthor.id)
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test that get returns null when book not found in the database`(){
        val result = underTest.get(BOOK_A_ISBN)
        assertThat(result).isNull()
    }

    @Test
    fun `test that get returns book when book is found in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val result = underTest.get(BOOK_A_ISBN)
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(savedBook)
    }

    @Test
    fun `test that partialUpdate throws IllegalStateException when the book does not exist in the database`(){
        assertThrows<IllegalStateException> {
            val bookUpdateRequest = BookUpdateRequest(
                title = "A new title"
            )
            underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)
        }
    }

    @Test
    fun `test that partialUpdate updates the title of an existing book`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val newTitle = "A new title"
        val bookUpdateRequest = BookUpdateRequest(
            title = newTitle)

        val result = underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)
        assertThat(result.title).isEqualTo(newTitle)
    }

    @Test
    fun `test that partialUpdate updates the description of an existing book`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val newDescription = "A new description"
        val bookUpdateRequest = BookUpdateRequest(
            description = newDescription)

        val result = underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)
        assertThat(result.description).isEqualTo(newDescription)
    }

    @Test
    fun `test that partialUpdate updates the image of an existing book`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        val newImage = "new-image.jpg"
        val bookUpdateRequest = BookUpdateRequest(
            image = newImage)

        val result = underTest.partialUpdate(BOOK_A_ISBN, bookUpdateRequest)
        assertThat(result.image).isEqualTo(newImage)
    }

    @Test
    fun `test that delete successfully a book in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(BOOK_A_ISBN, savedAuthor))
        assertThat(savedBook).isNotNull()

        underTest.delete(BOOK_A_ISBN)

        val result = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(result).isNull()
    }

    @Test
    fun `test that delete successfully a book that does not exist in the database`(){
        underTest.delete(BOOK_A_ISBN)

        val result = bookRepository.findByIdOrNull(BOOK_A_ISBN)
        assertThat(result).isNull()
    }
}