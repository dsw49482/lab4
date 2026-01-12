package com.example.lab56 // <--- ZMIEŃ NA SWOJĄ NAZWĘ PACZKI!

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- 1. MODEL DANYCH ---
data class Person(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val surname: String,
    val age: String, // Zmieniliśmy datę na wiek (łatwiej walidować 18-99)
    val phone: String,
    val email: String
)

// --- 2. WALIDATOR (LOGIKA BIZNESOWA) ---
object Validator {
    fun isEmailValid(email: String): Boolean = email.contains("@") && email.contains(".")

    fun isPhoneValid(phone: String): Boolean = phone.length == 9 && phone.all { it.isDigit() }

    // Nowy wymóg: wiek 18-99 lat
    fun isAgeValid(ageStr: String): Boolean {
        val age = ageStr.toIntOrNull() ?: return false
        return age in 18..99
    }
}

// --- 3. VIEWMODEL (REFAKTORYZACJA - ODDZIELENIE LOGIKI OD WIDOKU) ---
class MainViewModel : ViewModel() {
    // Lista osób (Stan)
    private val _people = mutableStateListOf<Person>()
    val people: List<Person> get() = _people

    // Osoba aktualnie edytowana (null = tryb dodawania)
    var currentPersonToEdit: Person? = null

    fun addOrUpdatePerson(name: String, surname: String, age: String, phone: String, email: String): Boolean {
        if (!Validator.isEmailValid(email) || !Validator.isPhoneValid(phone) || !Validator.isAgeValid(age)) {
            return false
        }

        if (currentPersonToEdit != null) {
            // Tryb Edycji: Znajdź starą osobę i podmień dane
            val index = _people.indexOfFirst { it.id == currentPersonToEdit!!.id }
            if (index != -1) {
                _people[index] = currentPersonToEdit!!.copy(
                    name = name, surname = surname, age = age, phone = phone, email = email
                )
            }
            currentPersonToEdit = null
        } else {
            // Tryb Dodawania: Dodaj nową
            _people.add(Person(name = name, surname = surname, age = age, phone = phone, email = email))
        }
        return true
    }

    fun deletePerson(person: Person) {
        _people.remove(person)
    }

    fun startEditing(person: Person) {
        currentPersonToEdit = person
    }

    fun clearEditMode() {
        currentPersonToEdit = null
    }
}

// --- 4. WIDOKI (UI) ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { MainApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel() // Wstrzyknięcie ViewModelu

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "") }, label = { Text("Home") }, selected = false, onClick = { navController.navigate("home") })
                NavigationBarItem(icon = { Icon(Icons.Default.Add, "") }, label = { Text("Dodaj") }, selected = false, onClick = {
                    viewModel.clearEditMode() // Reset formularza przy wejściu w Dodaj
                    navController.navigate("form")
                })
                NavigationBarItem(icon = { Icon(Icons.Default.List, "") }, label = { Text("Lista") }, selected = false, onClick = { navController.navigate("list") })
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(navController) }
            composable("form") { FormScreen(navController, viewModel) }
            composable("list") { ListScreen(navController, viewModel) }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Projekt Końcowy", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Wersja Release")
    }
}

@Composable
fun FormScreen(navController: NavController, viewModel: MainViewModel) {
    // Jeśli edytujemy, pobierz dane. Jeśli nie - puste pola.
    val personToEdit = viewModel.currentPersonToEdit

    var name by remember { mutableStateOf(personToEdit?.name ?: "") }
    var surname by remember { mutableStateOf(personToEdit?.surname ?: "") }
    var age by remember { mutableStateOf(personToEdit?.age ?: "") }
    var phone by remember { mutableStateOf(personToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(personToEdit?.email ?: "") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(if (personToEdit != null) "Edytuj Osobę" else "Dodaj Osobę", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth().testTag("field_name"))
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Nazwisko") }, modifier = Modifier.fillMaxWidth().testTag("field_surname"))
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Wiek (18-99)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth().testTag("field_age"))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon (9 cyfr)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth().testTag("field_phone"))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth().testTag("field_email"))

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val success = viewModel.addOrUpdatePerson(name, surname, age, phone, email)
            if (success) {
                Toast.makeText(context, "Zapisano!", Toast.LENGTH_SHORT).show()
                navController.navigate("list")
            } else {
                Toast.makeText(context, "Błąd! Sprawdź wiek (18-99), email i telefon.", Toast.LENGTH_LONG).show()
            }
        }, modifier = Modifier.fillMaxWidth().testTag("btn_save")) {
            Text("ZAPISZ")
        }
    }
}

@Composable
fun ListScreen(navController: NavController, viewModel: MainViewModel) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(viewModel.people) { person ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("${person.name} ${person.surname}, ${person.age} lat", style = MaterialTheme.typography.titleMedium)
                        Text("Tel: ${person.phone}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row {
                        // Przycisk EDYCJI
                        IconButton(onClick = {
                            viewModel.startEditing(person)
                            navController.navigate("form")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                        }
                        // Przycisk USUWANIA
                        IconButton(onClick = { viewModel.deletePerson(person) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}