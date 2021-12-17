package com.itmo.microservices.demo.warehouse

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.UserRequestDto
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class, MockitoExtension::class)
@ActiveProfiles("dev")
class WarehouseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun getSlotsTest() {
        val accessToken = getTestUserAccessToken("testUser", "testPassword")

        mockMvc.get("/items?available=true") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
        }

        mockMvc.get("/items?available=false") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
        }
    }

    private fun getTestUserAccessToken(name: String, password: String) : Any? {
        val request = UserRequestDto(name, password)

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }

        val authRequest = AuthenticationRequest(name, password)

        val result = mockMvc.post("/authentication") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("accessToken")
    }

}