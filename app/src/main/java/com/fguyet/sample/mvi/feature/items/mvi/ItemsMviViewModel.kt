package com.fguyet.sample.mvi.feature.items.mvi

import androidx.lifecycle.viewModelScope
import com.fguyet.sample.mvi.core.MviViewModel
import com.fguyet.sample.mvi.factory.ItemFactory
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch


/**
 * MVI ViewModel for the items screen.
 */
class ItemsMviViewModel(
    initialState: ItemsUiState = ItemsUiState()
) : MviViewModel<ItemsUiState, ItemsAction>(initialState) {
    private val itemFactory = ItemFactory()
    override fun handle(action: ItemsAction) {
        viewModelScope.launch {
            when (action) {
                is ItemsAction.AddItem -> addItem(action.name)
                is ItemsAction.DeleteItem -> deleteItem(action.id)
                ItemsAction.ConsumeError -> updateState { copy(errorMessage = null) }
                ItemsAction.ConsumeDeletionSuccess -> updateState { copy(deletionSuccess = false) }
            }
        }
    }

    private suspend fun addItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            updateState { copy(errorMessage = "Name cannot be empty.") }
            return
        }
        updateState { copy(items = items.toPersistentList().add(itemFactory.create(trimmed))) }
    }

    private suspend fun deleteItem(id: String) {
        updateState {
            if (items.none { it.id == id }) {
                copy(errorMessage = "Item not found.")
            } else {
                copy(
                    items = items.filter { it.id != id }.toPersistentList(),
                    deletionSuccess = true
                )
            }
        }
    }
}
