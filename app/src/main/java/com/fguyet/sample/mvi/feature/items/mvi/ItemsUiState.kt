package com.fguyet.sample.mvi.feature.items.mvi

import com.fguyet.sample.mvi.model.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ItemsUiState(
    val items: ImmutableList<Item> = persistentListOf(),
    val errorMessage: String? = null,
    val deletionSuccess: Boolean = false
)
