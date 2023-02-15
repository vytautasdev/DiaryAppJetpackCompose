package com.vytautasdev.mydiaryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.vytautasdev.auth.navigation.authenticationRoute
import com.vytautasdev.home.navigation.homeRoute
import com.vytautasdev.util.Screen
import com.vytautasdev.write.navigation.writeRoute

@Composable
fun SetupNavGraph(
    startDestination: String, navController: NavHostController, onDataLoaded: () -> Unit
) {
    NavHost(
        startDestination = startDestination, navController = navController
    ) {
        authenticationRoute(navigateToHome = {
            navController.popBackStack()
            navController.navigate(Screen.Home.route)
        }, onDataLoaded = onDataLoaded)
        homeRoute(navigateToWrite = {
            navController.navigate(Screen.Write.route)
        }, navigateToWriteWithArgs = {
            navController.navigate(Screen.Write.passDiaryId(diaryId = it))
        }, navigateToAuth = {
            navController.popBackStack()
            navController.navigate(Screen.Authentication.route)
        }, onDataLoaded = onDataLoaded
        )
        writeRoute(onBackPressed = {
            navController.popBackStack()
        })
    }
}




