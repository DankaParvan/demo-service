package com.itmo.microservices.demo.users.impl.repository

import com.itmo.microservices.demo.users.impl.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findUserByName(name: String): User?
}