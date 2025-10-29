package com.cs407.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingMenu() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Disable drawer gestures only when on map screen
    val isMapScreen = currentRoute == "map"

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isMapScreen,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                // Map
                NavigationDrawerItem(
                    label = { Text("Map") },
                    selected = currentRoute == "map",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("map") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Apartments
                NavigationDrawerItem(
                    label = { Text("Apartments") },
                    selected = currentRoute == "apartments",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("apartments") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Profile → LoginScreen
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = currentRoute == "profile" || currentRoute == "signUp",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Chat
                NavigationDrawerItem(
                    label = { Text("Chat") },
                    selected = currentRoute == "chat",
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("chat") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Drawer button
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }

                // Navigation Host
                NavHost(
                    navController = navController,
                    startDestination = "map",
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Core Screens
                    composable("map") { MapScreen() }
                    composable("apartments") { PlaceholderScreen("Apartments List") }
                    composable("chat") { PlaceholderScreen("Chat Interface") }

                    // 👇 Login + Sign Up Screens
                    composable("profile") { LoginScreen(navController) }
                    composable("signUp") { SignUpScreen(navController) }
                }
            }
        }
    }
}

// Simple placeholder for temporary screens
@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}
