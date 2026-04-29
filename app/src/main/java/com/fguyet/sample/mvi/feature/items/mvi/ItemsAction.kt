package com.fguyet.sample.mvi.feature.items.mvi

sealed interface ItemsAction {
    data class AddItem(val name: String) : ItemsAction
    data class DeleteItem(val id: String) : ItemsAction
    data object ConsumeError : ItemsAction
    data object ConsumeDeletionSuccess : ItemsAction
}