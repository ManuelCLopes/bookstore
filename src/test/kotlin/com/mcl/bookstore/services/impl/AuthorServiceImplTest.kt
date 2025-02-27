package com.mcl.bookstore.services.impl

import com.mcl.bookstore.domain.AuthorUpdateRequest
import com.mcl.bookstore.domain.entities.AuthorEntity
import com.mcl.bookstore.repositories.AuthorRepository
import com.mcl.bookstore.testAuthorEntityA
import com.mcl.bookstore.testAuthorEntityB
import com.mcl.bookstore.testAuthorUpdateA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalStateException

@SpringBootTest
@Transactional
class AuthorServiceImplTest @Autowired constructor(
    private val underTest: AuthorServiceImpl,
    private val authorRepository: AuthorRepository
    ) {

    @Test
    fun `test that save persists the Author in the database`(){
        val savedAuthor = underTest.create(testAuthorEntityA( ))
        assertThat(savedAuthor.id).isNotNull()

        val recalledAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
        assertThat(recalledAuthor).isNotNull()
        assertThat(recalledAuthor!!).isEqualTo(
            testAuthorEntityA(id = savedAuthor.id)
        )
    }

    @Test
    fun `test that list returns empty list when no authors in the database`(){
        val result = underTest.list()
        assertThat(result).isEmpty()
    }

    @Test
    fun `test that list returns empty list when authors in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val expected = listOf(savedAuthor)

        val result = underTest.list()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `test that get returns null when author not present in the database`(){
        val result = underTest.get(999)
        assertThat(result).isNull()
    }

    @Test
    fun `test that get returns author when author is present in the database`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())

        val result = underTest.get(savedAuthor.id!!)
        assertThat(result).isEqualTo(savedAuthor)
    }

    @Test
    fun `test that an Author with an ID throws an IllegalArgumentException`(){
        assertThrows<IllegalArgumentException> {
            val existingAuthor = testAuthorEntityA(id = 999)
            underTest.create(existingAuthor)
        }
    }

    @Test
    fun `test that full update successfully update an Author in the database`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!
        val updatedAuthor = testAuthorEntityB(id = existingAuthorId)

        val result = underTest.fullUpdate(existingAuthorId, updatedAuthor)
        assertThat(result).isEqualTo(updatedAuthor)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)
        assertThat(retrievedAuthor).isNotNull()
        assertThat(retrievedAuthor).isEqualTo(updatedAuthor)
    }

    @Test
    fun `test that full update Author throws IllegalStateException when Author does not exist in the database`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updatedAuthor = testAuthorEntityB(id=nonExistingAuthorId)

            underTest.fullUpdate(nonExistingAuthorId, updatedAuthor)
        }
    }

    @Test
    fun `test that partial update Author throws IllegalStateException when Author does not exist in the database`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updatedAuthor = testAuthorUpdateA(id=nonExistingAuthorId)

            underTest.partialUpdate(nonExistingAuthorId, updatedAuthor)
        }
    }

    @Test
    fun `test that partial update Author does not update Author when all values are null`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val updatedAuthor = underTest.partialUpdate(existingAuthor.id!!, AuthorUpdateRequest())

        assertThat(updatedAuthor).isEqualTo(existingAuthor)
    }

    @Test
    fun `test that partial update Author updates author name`(){
        val newName = "New Name"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(
            name = newName
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            name = newName
        )
        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor =expectedAuthor,
            authorUpdate = authorUpdateRequest
        )
    }

    @Test
    fun `test that partial update Author updates author age`(){
        val newAge = 50
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(
            age = newAge
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            age = newAge
        )
        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor =expectedAuthor,
            authorUpdate = authorUpdateRequest
        )
    }

    @Test
    fun `test that partial update Author updates author description`(){
        val newDescription = "new Description"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(
            description = newDescription
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            description = newDescription
        )
        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor =expectedAuthor,
            authorUpdate = authorUpdateRequest
        )
    }

    @Test
    fun `test that partial update Author updates author image`(){
        val newImage = "new Image"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(
            image = newImage
        )
        val authorUpdateRequest = AuthorUpdateRequest(
            image = newImage
        )
        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor =expectedAuthor,
            authorUpdate = authorUpdateRequest
        )
    }

    private fun assertThatAuthorPartialUpdateIsUpdated(
        existingAuthor: AuthorEntity,
        expectedAuthor: AuthorEntity,
        authorUpdate: AuthorUpdateRequest
    ) {
        //Save an existing Author
        val savedExistingAuthor = authorRepository.save(existingAuthor)
        val existingAuthorId = savedExistingAuthor.id!!

        //Update the Author
        val updatedAuthor = underTest.partialUpdate(existingAuthorId, authorUpdate)

        //Set up the expected Author
        val expected = expectedAuthor.copy(id = existingAuthorId)
        assertThat(updatedAuthor).isEqualTo(expected)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)

        assertThat(retrievedAuthor).isNotNull()
        assertThat(retrievedAuthor).isEqualTo(expected)
    }

    @Test
    fun `test that delete deletes an existing Author in the database`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!

        underTest.delete(existingAuthorId)

        assertThat(
            authorRepository.existsById(existingAuthorId)
        ).isFalse()
    }

    @Test
    fun `test that delete deletes a non-existing Author in the database`(){
        val nonExistingId = 999L

        underTest.delete(nonExistingId)

        assertThat(
            authorRepository.existsById(nonExistingId)
        ).isFalse()
    }

}