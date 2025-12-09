package com.cs407.myapplication.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.auth.LoginScreen
import com.cs407.myapplication.ui.auth.SignUpScreen
import com.cs407.myapplication.ui.chat.ChatScreen
import com.cs407.myapplication.ui.home.MapScreen
import com.cs407.myapplication.ui.profile.ProfileScreen
import com.cs407.myapplication.ui.roommates.RoommateBrowseScreen
import kotlinx.coroutines.launch

val apartmentList = listOf(
    Apartment("Waterfront Apartment", R.drawable.waterfront),
    Apartment("Palisade Properties", R.drawable.palisade),
    Apartment("Aberdeen Apartments", R.drawable.aberdeen),
    Apartment("140 Iota Courts", R.drawable.iota),
    Apartment("The Langdon Apartment", R.drawable.langdon)
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
                ModalDrawerSheet {
                    Text("Menu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                    HorizontalDivider()

                    NavigationDrawerItem(label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home") {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        }
                    )

                    NavigationDrawerItem(label = { Text("Apartments") },
                        selected = currentRoute == "apartments",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("apartments") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(label = { Text("Chat") },
                        selected = currentRoute == "chat",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("chat") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(label = { Text("Roommates") },
                        selected = currentRoute == "roommates",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("roommates") { launchSingleTop = true }
                        }
                    )

                    NavigationDrawerItem(label = { Text("Profile") },
                        selected = currentRoute == "profile",
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
                        .padding(top = 55.dp)  // make room for FAB
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "home" else "login",
                    ) {

                        composable("login") { LoginScreen(navController) }
                        composable("signUp") { SignUpScreen(navController) }
                        composable("home") { MapScreen() }
                        composable("chat") { ChatScreen() }
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

                            val apartment = apartmentList.find { it.name == apartmentName }
                                ?: Apartment("Unknown", R.drawable.waterfront)

                            ApartmentDetailScreen(
                                apartment = apartment,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 30.dp)
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
