package com.example.astro.app.passcorrect

class PasswordCorrect() {
    open fun isStrongPassword(password: String): Boolean {
        val digitRegex = ".*\\d.*"
        val letterRegex = ".*[a-zA-Z].*"

        return password.length >= 10 && password.matches(digitRegex.toRegex()) && password.matches(letterRegex.toRegex())
    }
}