package com.mehmetbaloglu.geminichatbot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mehmetbaloglu.geminichatbot.ui.screens.ChatScreen

@Composable
fun AppNavigation(){

    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = AppScreens.MainScreen.name
    ) {

        composable(route = AppScreens.MainScreen.name){
            ChatScreen(navController)
        }
    }
}