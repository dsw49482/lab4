package com.example.lab56

import org.junit.Test
import org.junit.Assert.*

// Testy sprawdzające logikę walidacji (Zadanie 1)
class ExampleUnitTest {

    // Test pozytywny: poprawny email
    @Test
    fun email_isCorrect() {
        assertTrue(Validator.isEmailValid("student@pwr.edu.pl"))
    }

    // Test negatywny: email bez małpy
    @Test
    fun email_isIncorrect() {
        assertFalse(Validator.isEmailValid("studentpwr.edu.pl"))
    }

    // Test pozytywny: poprawny telefon (9 cyfr)
    @Test
    fun phone_isCorrect() {
        assertTrue(Validator.isPhoneValid("123456789"))
    }

    // Test negatywny: za krótki telefon
    @Test
    fun phone_isIncorrect() {
        assertFalse(Validator.isPhoneValid("123"))
    }
}