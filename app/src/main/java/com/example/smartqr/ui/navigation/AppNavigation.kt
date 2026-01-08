package com.example.smartqr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartqr.ui.generate.GenerateScreen
import com.example.smartqr.ui.scan.ScanScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Scan,
        NavigationItem.Generate
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Scan.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Scan.route) {
                ScanScreen()
            }
            composable(NavigationItem.Generate.route) {
                GenerateScreen()
            }
        }
    }
}

sealed class NavigationItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Scan : NavigationItem("scan", "Scanner", Icons.Default.QrCodeScanner)
    object Generate : NavigationItem("generate", "Generator", Icons.Default.Create)
}