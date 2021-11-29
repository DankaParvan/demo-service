package com.itmo.microservices.demo.users.impl.service

import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.common.exception.AccessDeniedException
import com.itmo.microservices.demo.users.api.model.*
import com.itmo.microservices.demo.users.api.service.IUserService
import com.itmo.microservices.demo.users.impl.entity.User
import com.itmo.microservices.demo.users.impl.logging.UserServiceNotableEvents
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tokenManager: JwtTokenManager,
    private val passwordEncoder: PasswordEncoder
) : IUserService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    private fun findUser(name: String) = userRepository.findUserByName(name)?.toModel()

    override fun addUser(userModel: UserModel): ResponseEntity<UserResponseDto> {
        val user = userRepository.findUserByName(userModel.name)

        return if (user != null ) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } else {
            val savedUser = userRepository.save(userModel.toEntity())
            eventLogger.info(UserServiceNotableEvents.I_USER_CREATED, userModel.name)

            ResponseEntity(UserResponseDto(UUID.fromString(savedUser.id), savedUser.name), HttpStatus.OK)
        }
    }

    override fun getUserById(id: UUID): UserResponseDto? {
        val user = userRepository.findById(id.toString())

        return if (!user.isEmpty) {
            UserResponseDto(UUID.fromString(user.get().id), user.get().name)
        } else {
            null
        }
    }

    override fun authUser(request: AuthenticationRequest): AuthenticationResult {
        val user = findUser(request.username)

        if (request.password != user?.password)
            throw AccessDeniedException("Invalid password")

        val accessToken = tokenManager.generateToken(user.userDetails())
        val refreshToken = tokenManager.generateRefreshToken(user.userDetails())

        return AuthenticationResult(accessToken, refreshToken)
    }

    override fun refreshToken(authentication: Authentication): AuthenticationResult {
        val refreshToken = authentication.credentials as String
        val principal = authentication.principal as UserDetails
        val accessToken = tokenManager.generateToken(principal)

        return AuthenticationResult(accessToken, refreshToken)
    }

    private fun UserModel.toEntity() = User(this.name, this.password, this.status)

    private fun User.toModel() =
        UserModel(
            this.name,
            this.password,
            this.status
        )
}
