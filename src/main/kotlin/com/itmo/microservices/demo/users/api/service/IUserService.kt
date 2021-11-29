package com.itmo.microservices.demo.users.api.service

import com.itmo.microservices.demo.users.api.model.AuthenticationRequest
import com.itmo.microservices.demo.users.api.model.AuthenticationResult
import com.itmo.microservices.demo.users.api.model.UserModel
import com.itmo.microservices.demo.users.api.model.UserResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import java.util.*

interface IUserService {
    fun addUser(userModel: UserModel): ResponseEntity<UserResponseDto>
    fun getUserById(id: UUID): UserResponseDto?
    fun authUser(request: AuthenticationRequest): AuthenticationResult
    fun refreshToken(authentication: Authentication): AuthenticationResult
}