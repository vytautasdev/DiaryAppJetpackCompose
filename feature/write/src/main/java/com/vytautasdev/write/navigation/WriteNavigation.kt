package com.vytautasdev.write.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.vytautasdev.util.Constants
import com.vytautasdev.util.Screen
import com.vytautasdev.util.model.Mood
import com.vytautasdev.write.WriteScreen
import com.vytautasdev.write.WriteViewModel

@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = Constants.WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val viewModel: WriteViewModel = hiltViewModel()
        val context = LocalContext.current
        val uiState = viewModel.uiState
        val galleryState = viewModel.galleryState
        val pagerState = rememberPagerState()
        val pageIndex by remember {
            derivedStateOf { pagerState.currentPage }
        }

        WriteScreen(uiState = uiState, moodName = {
            Mood.values()[pageIndex].name
        }, pagerState = pagerState, galleryState = galleryState, onDeleteConfirmed = {
            viewModel.deleteDiary(onSuccess = {
                Toast.makeText(context, "Entry deleted successfully!", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }, onError = { error ->
                Toast.makeText(context, "An error occurred: $error", Toast.LENGTH_SHORT).show()
            })
        }, onDateTimeUpdated = {
            viewModel.updateDateTime(zonedDateTime = it)
        }, onBackPressed = onBackPressed, onTitleChanged = {
            viewModel.setTitle(title = it)
        }, onDescriptionChanged = {
            viewModel.setDescription(description = it)
        }, onSaveClicked = {
            viewModel.upsertDiary(diary = it.apply { mood = Mood.values()[pageIndex].name },
                onSuccess = { onBackPressed() },
                onError = { error ->
                    Log.d("deleteImg", "writeRoute: $error")
                    Toast.makeText(context, "An error occurred: $error", Toast.LENGTH_SHORT).show()
                })
        }, onImageSelect = {
            // A function to dynamically get the actual file extension of image Uri, i.e. jpg, png etc.
            val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
            Log.d("WriteViewModel", "URI: $it")

            viewModel.addImage(
                image = it, imageType = type
            )
        }, onImageDeleteClicked = { galleryState.removeImage(it) })
    }
}