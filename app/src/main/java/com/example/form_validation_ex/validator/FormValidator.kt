package com.example.form_validation_ex.validator

object FormValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    private val PHONE_REGEX = Regex("^(03|05|07|08|09)\\d{8}$")

    fun isValidName(name: String): Boolean = name.trim().length >= 3

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && EMAIL_REGEX.matches(email.trim())

    fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace("\\s".toRegex(), "")
        return PHONE_REGEX.matches(cleanPhone)
    }

    fun isValidPassword(pass: String): Boolean = pass.length >= 6

    fun isPasswordMatch(pass: String, confirmPass: String): Boolean =
        confirmPass.isNotEmpty() && pass == confirmPass
}