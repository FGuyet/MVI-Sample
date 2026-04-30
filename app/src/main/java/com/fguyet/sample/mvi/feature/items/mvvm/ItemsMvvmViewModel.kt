package com.fguyet.sample.mvi.feature.items.mvvm

import androidx.lifecycle.ViewModel
import com.fguyet.sample.mvi.factory.ItemFactory
import com.fguyet.sample.mvi.model.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * MVVM ViewModel for the items screen.
 */
class ItemsMvvmViewModel(
    initialItems: List<Item> = emptyList()
) : ViewModel() {
    private val itemFactory = ItemFactory()

    private val _items: MutableStateFlow<PersistentList<Item>> = MutableStateFlow(initialItems.toPersistentList())

    val items: StateFlow<ImmutableList<Item>> = _items

    /** Adds an item. Returns the created [Item] on success, or a failure with an error message. */
    fun addItem(name: String): Result<Item> {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be empty."))
        val item = itemFactory.create(trimmed)
        _items.update { it.add(item) }
        return Result.success(item)
    }

    /** Deletes an item by [id]. Returns the deleted id on success, or a failure with an error message. */
    fun deleteItem(id: String): Result<String> {
        if (_items.value.none { it.id == id }) return Result.failure(IllegalArgumentException("Item not found."))
        _items.update { list -> list.filter { it.id != id }.toPersistentList() }
        return Result.success(id)
    }
}
