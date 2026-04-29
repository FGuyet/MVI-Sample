package com.fguyet.sample.mvi.feature.items.mvi

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fguyet.sample.mvi.feature.items.ItemsScreen

@Composable
fun ItemsMviRoute(
    modifier: Modifier = Modifier,
    viewModel: ItemsMviViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Show error toast and reset the state field once rendered
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.handle(ItemsAction.ConsumeError)
        }
    }

    // Show deletion-success toast and reset the state field once rendered
    LaunchedEffect(state.deletionSuccess) {
        if (state.deletionSuccess) {
            Toast.makeText(context, "Item deleted.", Toast.LENGTH_SHORT).show()
            viewModel.handle(ItemsAction.ConsumeDeletionSuccess)
        }
    }

    ItemsScreen(
        modifier = modifier,
        title = "MVI",
        items = state.items,
        onAddItem = { name -> viewModel.handle(ItemsAction.AddItem(name)) },
        onDeleteItem = { viewModel.handle(ItemsAction.DeleteItem(it)) }
    )
}

