package com.example.lab56

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddingPerson() {
        // 1. Uruchom aplikację
        composeTestRule.setContent {
            MainApp()
        }

        // 2. Przejdź do ekranu dodawania (kliknij "Dodaj" w pasku na dole)
        composeTestRule.onNodeWithText("Dodaj").performClick()

        // 3. Wpisz dane w pola (szukamy po tagach, które dodałem w MainActivity)
        composeTestRule.onNodeWithTag("field_name").performTextInput("Jan")
        composeTestRule.onNodeWithTag("field_surname").performTextInput("Testowy")
        composeTestRule.onNodeWithTag("field_phone").performTextInput("123456789")
        composeTestRule.onNodeWithTag("field_email").performTextInput("jan@test.pl")

        // 4. Kliknij ZAPISZ
        composeTestRule.onNodeWithTag("btn_save").performClick()

        // 5. Przejdź do listy
        composeTestRule.onNodeWithText("Lista").performClick()

        // 6. Sprawdź, czy "Jan Testowy" jest na liście
        composeTestRule.onNodeWithText("Jan Testowy").assertExists()
    }
}