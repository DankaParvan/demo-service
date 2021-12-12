package com.itmo.microservices.demo.payment.api

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
class PaymentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun executePaymentTest() {
        val orderId = addTestOrder("paymentTestUser", "PaymentTestPass")
        val accessToken = getAccessToken("paymentTestUser", "PaymentTestPass")

        mockMvc.post("/orders/$orderId/payment") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
    }

    @Test
    fun finlogAllTest() {
        addTestUser("finlogAllTestUser", "finlogAllTestPass")
        val accessToken = getAccessToken("finlogAllTestUser", "finlogAllTestPass")

        mockMvc.get("/orders/finlog") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
    }

    private fun addTestUser(name: String, password: String): Any? {
        val request = UserRequestDto(name, password)

        val result = mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("id")
    }

    private fun getAccessToken(name: String, password: String): Any? {
        val authRequest = AuthenticationRequest(name, password)

        val result = mockMvc.post("/authentication") {
            contentType = MediaType.APPLICATION_JSON
            content = jacksonObjectMapper().writeValueAsString(authRequest)
            accept = MediaType.APPLICATION_JSON
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("accessToken")
    }

    private fun addTestOrder(name: String, password: String): Any? {
        addTestUser(name, password)
        val accessToken = getAccessToken(name, password)

        val result = mockMvc.post("/orders") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andReturn()

        return JSONObject(result.response.contentAsString).get("uuid")
    }
}