package com.itmo.microservices.demo.users.api.model

import java.util.*

data class UserResponseDto(
    val id: UUID,
    val name: String
)