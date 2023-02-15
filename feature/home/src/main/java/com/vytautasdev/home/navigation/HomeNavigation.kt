package com.vytautasdev.home.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vytautasdev.home.HomeScreen
import com.vytautasdev.home.HomeViewModel
import com.vytautasdev.mongo.repository.MongoDB
import com.vytautasdev.ui.components.DisplayAlertDialog
import com.vytautasdev.util.Constants.APP_ID
import com.vytautasdev.util.Screen
import com.vytautasdev.util.model.RequestState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    navigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit,
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var signOutDialogOpened by remember {
            mutableStateOf(false)
        }
        var deleteAllDialogOpened by remember {
            mutableStateOf(false)
        }


        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onSignOutClicked = {
                signOutDialogOpened = true
            },
            onDeleteAllClicked = {
                deleteAllDialogOpened = true
            },
            navigateToWrite = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs,
            onDateSelected = {
                viewModel.getDiaries(zonedDateTime = it)
            },
            onDateReset = { viewModel.getDiaries() },
            dateIsSelected = viewModel.dateIsSelected
        )

        LaunchedEffect(key1 = Unit) {
            MongoDB.configureTheRealm()
        }

        DisplayAlertDialog(title = "Sign Out",
            message = "Are you sure you want to sign out?",
            dialogOpened = signOutDialogOpened,
            onCloseDialog = {
                signOutDialogOpened = false
            },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateToAuth()
                        }
                    }
                }
            })


        DisplayAlertDialog(title = "Delete All Diaries",
            message = "Are you sure you want to delete all your diaries?",
            dialogOpened = deleteAllDialogOpened,
            onCloseDialog = {
                deleteAllDialogOpened = false
            },
            onYesClicked = {
                viewModel.deleteAllDiaries(onSuccess = {
                    android.widget.Toast.makeText(
                        context,
                        "All diaries successfully deleted!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    scope.launch { drawerState.close() }
                }, onError = {
                    android.widget.Toast.makeText(
                        context,
                        if (it.message == "No Internet connection. Please try again.") "You must be connected to the internet to proceed." else it.message,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    scope.launch { drawerState.close() }
                })
            })
    }
}