package com.example.lab56

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- 1. MODEL DANYCH [cite: 103] ---
data class Person(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val surname: String,
    val dob: String,
    val phone: String,
    val email: String,
    val address: String
)

// Prosta "baza danych" w pamięci (List)
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

    // UI: Ekran z dolnym paskiem [cite: 100]
    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple("Home", "home", Icons.Default.Home),
                    Triple("Dodaj", "add", Icons.Default.Add),
                    Triple("Lista", "list", Icons.Default.List),
                    Triple("Usuń", "delete", Icons.Default.Delete)
                )
                items.forEach { (label, route, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = false,
                        onClick = { navController.navigate(route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Nawigacja między ekranami [cite: 101]
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("add") { AddScreen(navController) }
            composable("list") { ListScreen() }
            composable("delete") { DeleteScreen() }
        }
    }
}

// --- EKRAN GŁÓWNY [cite: 99] ---
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menu Główne", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate("add") }, modifier = Modifier.width(200.dp)) {
            Text("Dodaj osobę")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.navigate("list") }, modifier = Modifier.width(200.dp)) {
            Text("Wyświetl listę")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { navController.navigate("delete") }, modifier = Modifier.width(200.dp)) {
            Text("Usuń osobę")
        }
    }
}

// --- EKRAN DODAWANIA ---
@Composable
fun AddScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        Text("Dodaj Osobę", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Nazwisko") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Data urodzenia") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Adres") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (name.isNotEmpty() && surname.isNotEmpty()) {
                Database.people.add(Person(name = name, surname = surname, dob = dob, phone = phone, email = email, address = address))
                Toast.makeText(context, "Dodano!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Back button action
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("ZAPISZ")
        }
    }
}

// --- EKRAN LISTY ---
@Composable
fun ListScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista Osób", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(Database.people) { person ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${person.name} ${person.surname}", style = MaterialTheme.typography.titleMedium)
                        Text("Tel: ${person.phone}, Email: ${person.email}")
                    }
                }
            }
        }
    }
}

// --- EKRAN USUWANIA ---
@Composable
fun DeleteScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kliknij, aby usunąć", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(Database.people) { person ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { Database.people.remove(person) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${person.name} ${person.surname} [USUŃ]", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}