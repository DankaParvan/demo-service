package com.itmo.microservices.demo.users.impl.entity

import com.itmo.microservices.demo.users.api.model.Status
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    var id: String,
    var name: String,
    var password: String,
    var status: Status,
) {
    constructor() : this(UUID.randomUUID().toString(), "", "", Status.OFFLINE)

    constructor(
        name: String,
        password: String,
        status: Status,
    ) : this(UUID.randomUUID().toString(), name, password, status)
}