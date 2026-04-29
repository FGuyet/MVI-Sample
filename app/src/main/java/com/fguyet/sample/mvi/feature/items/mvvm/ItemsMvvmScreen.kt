package com.fguyet.sample.mvi.feature.items.mvvm

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fguyet.sample.mvi.feature.items.ItemsScreen

@Composable
fun ItemsMvvmRoute(
    modifier: Modifier = Modifier,
    viewModel: ItemsMvvmViewModel = viewModel()
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ItemsScreen(
        modifier = modifier,
        title = "MVVM",
        items = items,
        onAddItem = { name ->
            viewModel.addItem(name).onFailure { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        },
        onDeleteItem = { id ->
            viewModel.deleteItem(id)
                .onSuccess { Toast.makeText(context, "Item deleted.", Toast.LENGTH_SHORT).show() }
                .onFailure { e -> Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show() }
        }
    )
}
