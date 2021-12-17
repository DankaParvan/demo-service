package com.itmo.microservices.demo.users.api.controller

import com.itmo.microservices.demo.users.api.model.*
import com.itmo.microservices.demo.users.api.service.IUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*


@RestController
class UserController(private val userService: IUserService) {

    @PostMapping("/users")
    @Operation(
        summary = "Register new user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "User already exists", responseCode = "500")
        ]
    )
    fun addUser(@RequestBody request: UserRequestDto): ResponseEntity<UserResponseDto> {
        return userService.addUser(request.toModel())
    }

    @GetMapping("/users/{user_id}")
    @Operation(
        summary = "Get user by id",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getUserById(@PathVariable(value = "user_id") id: String) = userService.getUserById(UUID.fromString(id))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

    @PostMapping("/authentication")
    @Operation(
        summary = "Authenticate",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "User not found", responseCode = "404", content = [Content()]),
            ApiResponse(description = "Invalid password", responseCode = "403", content = [Content()])
        ]
    )
    fun authUser(@RequestBody request: AuthenticationRequest): AuthenticationResult {
       return userService.authUser(request)
    }

    @PostMapping("/authentication/refresh")
    @Operation(
        summary = "Refresh authentication",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Authentication error", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun refreshToken(authentication: Authentication) = userService.refreshToken(authentication)

    private fun UserRequestDto.toModel() = UserModel(this.name, this.password, Status.OFFLINE)
}