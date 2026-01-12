package com.example.lab56

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- LOGIKA DO TESTOWANIA (ZADANIE 1) ---
object Validator {
    fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
    fun isPhoneValid(phone: String): Boolean {
        return phone.length == 9 && phone.all { it.isDigit() }
    }
}

data class Person(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val surname: String,
    val phone: String,
    val email: String
)

object Database {
    val people = mutableStateListOf<Person>()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "") }, label = { Text("Home") }, selected = false, onClick = { navController.navigate("home") })
                NavigationBarItem(icon = { Icon(Icons.Default.Add, "") }, label = { Text("Dodaj") }, selected = false, onClick = { navController.navigate("add") })
                NavigationBarItem(icon = { Icon(Icons.Default.List, "") }, label = { Text("Lista") }, selected = false, onClick = { navController.navigate("list") })
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(navController) }
            composable("add") { AddScreen(navController) }
            composable("list") { ListScreen() }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Menu Główne")
        Button(onClick = { navController.navigate("add") }) { Text("Dodaj osobę") }
    }
}

@Composable
fun AddScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Dodajemy testTag, żeby test UI mógł znaleźć te pola
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Imię") }, modifier = Modifier.testTag("field_name"))
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Nazwisko") }, modifier = Modifier.testTag("field_surname"))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon") }, modifier = Modifier.testTag("field_phone"))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.testTag("field_email"))

        Button(onClick = {
            if (Validator.isEmailValid(email) && Validator.isPhoneValid(phone)) {
                Database.people.add(Person(name = name, surname = surname, phone = phone, email = email))
                Toast.makeText(context, "Dodano!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Błędne dane!", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.testTag("btn_save")) {
            Text("ZAPISZ")
        }
    }
}

@Composable
fun ListScreen() {
    LazyColumn {
        items(Database.people) { person ->
            Text(text = "${person.name} ${person.surname}", modifier = Modifier.testTag("item_person"))
        }
    }
}