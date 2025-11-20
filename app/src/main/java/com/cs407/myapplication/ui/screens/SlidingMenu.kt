package com.cs407.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.myapplication.R
import com.cs407.myapplication.ui.auth.AuthManager
import kotlinx.coroutines.launch

val apartmentList = listOf(
    Apartment("Waterfront Apartment", R.drawable.waterfront),
    Apartment("Palisade Properties", R.drawable.palisade),
    Apartment("Aberdeen Apartments", R.drawable.aberdeen)
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingMenu() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Authentication check (simplified)
    val isLoggedIn = AuthManager.auth.currentUser != null

    // Show drawer only when logged in
    val drawerEnabled = isLoggedIn && currentRoute !in listOf("login", "signUp")

    // Close drawer on login or signUp screens
    LaunchedEffect(currentRoute) {
        if (currentRoute == "login" || currentRoute == "signUp") {
            drawerState.close()
        }
    }

    // Keep drawer closed on first arrival to home screen after login
    LaunchedEffect(currentRoute, isLoggedIn) {
        if (currentRoute == "home" && isLoggedIn) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled,
        drawerContent = {
            if (drawerEnabled) {
                ModalDrawerSheet {

                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()

                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Apartments") },
                        selected = currentRoute == "apartments",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("apartments") {
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Chat") },
                        selected = currentRoute == "chat",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("chat") {
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Roommates") },
                        selected = currentRoute == "roommates",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("roommates") {
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = currentRoute == "profile",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {

        Scaffold(
            topBar = {
                if (drawerEnabled) {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) "home" else "login",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable("login") {
                    LoginScreen(navController)
                }

                composable("signUp") {
                    SignUpScreen(navController)
                }

                composable("home") {
                    MapScreen()
                }

                composable("apartments") {
                    ApartmentsListScreen(
                        onApartmentClick = { apartment ->
                            // Navigate to apartment detail with the apartment name as parameter
                            navController.navigate("apartmentDetail/${apartment.name}")
                        }
                    )
                }

                // Add the apartment detail screen with parameter
                composable(
                    "apartmentDetail/{apartmentName}",
                    arguments = listOf(navArgument("apartmentName") { type = NavType.StringType })
                ) { backStackEntry ->
                    val apartmentName = backStackEntry.arguments?.getString("apartmentName") ?: ""

                    val apartment = apartmentList.find { it.name == apartmentName }
                        ?: Apartment("Unknown", R.drawable.waterfront) // fallback

                    ApartmentDetailScreen(
                        apartment = apartment,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("chat") {
                    ChatScreen()
                }

                composable("roommates") {
                    RoommateBrowseScreen()
                }

                composable("profile") {
                    ProfileScreen(
                        onLogout = {
                            AuthManager.auth.signOut()

                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }

                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    }
}

data class Apartment(
    val name: String,
    val imageRes: Int
)