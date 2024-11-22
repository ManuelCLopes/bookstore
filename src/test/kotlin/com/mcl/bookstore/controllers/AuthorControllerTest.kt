package com.mcl.bookstore.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mcl.bookstore.domain.dto.AuthorUpdateRequestDto
import com.mcl.bookstore.domain.entities.AuthorEntity
import com.mcl.bookstore.services.AuthorService
import com.mcl.bookstore.testAuthorDtoA
import com.mcl.bookstore.testAuthorEntityA
import com.mcl.bookstore.testAuthorUpdateDtoA
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.put

private const val AUTHORS_BASE_URL = "/v1/authors"

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest @Autowired constructor (
    private val mockMvc: MockMvc,
    @MockkBean val authorService: AuthorService
    ) {

    val objectMapper = ObjectMapper()

    @BeforeEach
    fun beforeEach(){
        every {
            authorService.create(any())
        } answers {
            firstArg()
        }
    }

    @Test
    fun `test that create Author saves the Author`(){
        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }

        val expected = AuthorEntity(
            id = null,
            name = "John Doe",
            age = 30,
            image = "author-image.jpg",
            description = "Some description"
        )

        verify{authorService.create(expected)}
    }

    @Test
    fun `test that create Author returns a HTTP 201 status on a successful create`(){
        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `test that create Author returns HTTP 400 when IllegalArgumentException is thrown`(){
        every {
            authorService.create(any())
        } throws (IllegalArgumentException())

        mockMvc.post(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isBadRequest() }
        }

    }

    @Test
    fun `test that list returns an empty list and HTTP 200 when no authors in the database`(){
        every {
            authorService.list()
        } answers {
            emptyList()
        }

        mockMvc.get(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test that list returns an empty list and HTTP 200 when authors in the database`(){
        every {
            authorService.list()
        } answers {
            listOf(testAuthorEntityA(1))
        }

        mockMvc.get(AUTHORS_BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", equalTo(1)) }
            content { jsonPath("$[0].name", equalTo("John Doe")) }
            content { jsonPath("$[0].age", equalTo(30)) }
            content { jsonPath("$[0].description", equalTo("Some description")) }
            content { jsonPath("$[0].image", equalTo("author-image.jpg")) }
        }
    }

    @Test
    fun `test that get returns HTTP 404 when author not found in database`(){
        every {
            authorService.get(any())
        } answers {
            null
        }

        mockMvc.get("$AUTHORS_BASE_URL/999" ){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test that get returns HTTP 200 and author when author found in database`(){
        every {
            authorService.get(any())
        } answers {
            testAuthorEntityA(id=999)
        }

        mockMvc.get("$AUTHORS_BASE_URL/999" ){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpg")) }
        }
    }

    @Test
    fun `test that full update Author returns HTTP 200 and updated Author on successful call`() {
        every {
            authorService.fullUpdate(any(), any())
        } answers {
            secondArg()
        }

        mockMvc.put("$AUTHORS_BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id = 999))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpg")) }
        }
    }

    @Test
    fun `test that full update Author returns HTTP 400 on IllegalStateException`() {
        every {
            authorService.fullUpdate(any(), any())
        } throws (IllegalStateException())

        mockMvc.put("$AUTHORS_BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id = 999))
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that partial update Author returns HTTP 400 on IllegalStateException`() {
        every {
            authorService.partialUpdate(any(), any())
        } throws (IllegalStateException())

        mockMvc.patch("$AUTHORS_BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorUpdateDtoA(999L)
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that partial update returns HTTP 200 and updated Author`() {
        every {
            authorService.partialUpdate(any(), any())
        } answers {
            testAuthorEntityA(id = 999)
        }

        mockMvc.patch("$AUTHORS_BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorUpdateDtoA(999L)
            )
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", equalTo(999)) }
            content { jsonPath("$.name", equalTo("John Doe")) }
            content { jsonPath("$.age", equalTo(30)) }
            content { jsonPath("$.description", equalTo("Some description")) }
            content { jsonPath("$.image", equalTo("author-image.jpg")) }
        }
    }

    @Test
    fun `test that delete Author returns HTTP 204 on successful delete`(){
        every {
            authorService.delete(any())
        } answers {}

        mockMvc.delete("$AUTHORS_BASE_URL/999") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent()}
        }
    }
}