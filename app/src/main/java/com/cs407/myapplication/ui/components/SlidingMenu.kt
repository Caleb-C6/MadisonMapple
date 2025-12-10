package com.cs407.myapplication.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.myapplication.R
import com.cs407.myapplication.ui.apartments.ApartmentDetailScreen
import com.cs407.myapplication.ui.apartments.ApartmentsListScreen
import com.cs407.myapplication.ui.apartments.ApartmentNameMapper
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.auth.LoginScreen
import com.cs407.myapplication.ui.auth.SignUpScreen
import com.cs407.myapplication.ui.chat.ChatScreen
import com.cs407.myapplication.ui.home.MapScreen
import com.cs407.myapplication.ui.profile.ProfileScreen
import com.cs407.myapplication.ui.roommates.RoommateBrowseScreen
import kotlinx.coroutines.launch

// Update the apartment list to use display names from the mapper
val apartmentList = ApartmentNameMapper.getAllDisplayNames().map { displayName ->
    // Helper function to get image resource for display name
    val imageRes = when (displayName) {
        "Waterfront Apartment" -> R.drawable.waterfront
        "Palisade Properties" -> R.drawable.palisade
        "Aberdeen Apartments" -> R.drawable.aberdeen
        "140 Iota Courts" -> R.drawable.iota
        "The Langdon Apartment" -> R.drawable.langdon
        else -> R.drawable.waterfront // default
    }
    Apartment(displayName, imageRes)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingMenu() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoggedIn = AuthManager.auth.currentUser != null
    val drawerEnabled = isLoggedIn && currentRoute !in listOf("login", "signUp")
    val isMapScreen = currentRoute == "home"

    LaunchedEffect(currentRoute) {
        if (currentRoute == "login" || currentRoute == "signUp") drawerState.close()
    }

    LaunchedEffect(currentRoute, isLoggedIn) {
        if (currentRoute == "home" && isLoggedIn) drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled && !isMapScreen,
        drawerContent = {
            if (drawerEnabled) {
                ModalDrawerSheet(
                    drawerContainerColor = Color(0xFFD32F2F), // Red background
                    drawerContentColor = Color.White // White text
                ) {
                    Text(
                        "Menu",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White // White text for title
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.5f))

                    NavigationDrawerItem(
                        label = { Text("Home", color = Color.White) },
                        selected = currentRoute == "home",
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFB71C1C), // Darker red when selected
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Apartments", color = Color.White) },
                        selected = currentRoute == "apartments",
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFB71C1C),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("apartments") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Chat", color = Color.White) },
                        selected = currentRoute == "chat",
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFB71C1C),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("chat") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Roommates", color = Color.White) },
                        selected = currentRoute == "roommates",
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFB71C1C),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("roommates") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Profile", color = Color.White) },
                        selected = currentRoute == "profile",
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFB71C1C),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("profile") { launchSingleTop = true }
                        }
                    )
                }
            }
        }
    ) {

        Box(Modifier.fillMaxSize()) {

            /** ---------- MAIN SCREEN CONTENT ---------- */
            Scaffold { innerPadding ->
                Box(
                    Modifier
                        .padding(top = 70.dp)  // Increased from 55.dp to make more room for FAB
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "home" else "login",
                    ) {

                        composable("login") { LoginScreen(navController) }
                        composable("signUp") { SignUpScreen(navController) }
                        composable("home") { MapScreen() }

                        composable("chat") {
                            ChatScreen(
                                onBackClick = {
                                    navController.navigate("home") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable("roommates") { RoommateBrowseScreen() }

                        composable("apartments") {
                            ApartmentsListScreen(
                                onApartmentClick = { apartment ->
                                    navController.navigate("apartmentDetail/${apartment.name}")
                                }
                            )
                        }

                        composable(
                            "apartmentDetail/{apartmentName}",
                            arguments = listOf(navArgument("apartmentName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val apartmentName = backStackEntry.arguments?.getString("apartmentName") ?: ""

                            // Find the apartment from our list (optional, for image)
                            val apartment = apartmentList.find { it.name == apartmentName }
                                ?: Apartment("Unknown", R.drawable.waterfront)

                            ApartmentDetailScreen(
                                apartmentName = apartmentName,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                onLogout = {
                                    AuthManager.auth.signOut()
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                onAccountDeleted = {
                                    AuthManager.auth.signOut()
                                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                                }
                            )
                        }
                    }
                }
            }

            /** ---------- FLOATING MENU BUTTON ---------- */
            if (drawerEnabled) {
                FloatingActionButton(
                    onClick = { scope.launch { drawerState.open() } },
                    containerColor = Color(0xFFD32F2F), // Red background to match menu
                    contentColor = Color.White, // White icon
                    modifier = Modifier
                        .padding(start = 20.dp, top = 50.dp)  // Adjusted top padding
                        .align(Alignment.TopStart)
                        .size(35.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
            }
        }
    }
}

data class Apartment(
    val name: String,
    val imageRes: Int
)