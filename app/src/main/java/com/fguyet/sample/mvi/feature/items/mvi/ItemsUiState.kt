package com.fguyet.sample.mvi.feature.items.mvi

import com.fguyet.sample.mvi.model.Item

data class ItemsUiState(
    val items: List<Item> = emptyList(),
    val errorMessage: String? = null,
    val deletionSuccess: Boolean = false
)
