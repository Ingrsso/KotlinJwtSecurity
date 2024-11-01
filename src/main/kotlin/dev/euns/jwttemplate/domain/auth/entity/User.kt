package dev.euns.jwttemplate.domain.auth.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column
    var username: String? = null

    @Column
    var password: String? = null
}