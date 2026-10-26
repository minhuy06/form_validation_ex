package com.example.form_validation_ex.model

import java.io.Serializable

data class User(
    val fullName: String,
    val email: String,
    val phone: String,
    val gender: String,
    val password: String
) : Serializable