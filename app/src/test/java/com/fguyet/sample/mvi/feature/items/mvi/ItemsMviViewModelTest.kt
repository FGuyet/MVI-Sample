package com.fguyet.sample.mvi.feature.items.mvi

import com.fguyet.sample.mvi.model.Item
import com.fguyet.sample.mvi.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ItemsMviViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ItemsMviViewModel

    @Before
    fun setUp() {
        viewModel = ItemsMviViewModel()
    }

    //region Initial state

    @Test
    fun `initial state has empty items list`() {
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `initial state has no error message`() {
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `initial state has deletionSuccess false`() {
        assertFalse(viewModel.uiState.value.deletionSuccess)
    }

    //endregion

    //region AddItem

    @Test
    fun `AddItem with valid name adds item to the list`() = runTest {
        viewModel.handle(ItemsAction.AddItem("Widget"))

        val items = viewModel.uiState.value.items
        assertEquals(1, items.size)
        assertEquals("Widget", items.first().name)
    }

    @Test
    fun `AddItem trims whitespace before adding`() = runTest {
        viewModel.handle(ItemsAction.AddItem("  Gadget  "))

        assertEquals("Gadget", viewModel.uiState.value.items.first().name)
    }

    @Test
    fun `AddItem with blank name sets an error message`() = runTest {
        viewModel.handle(ItemsAction.AddItem("   "))

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `AddItem with empty string sets an error message`() = runTest {
        viewModel.handle(ItemsAction.AddItem(""))

        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `AddItem multiple times accumulates items`() = runTest {
        viewModel.handle(ItemsAction.AddItem("Alpha"))
        viewModel.handle(ItemsAction.AddItem("Beta"))
        viewModel.handle(ItemsAction.AddItem("Gamma"))

        assertEquals(3, viewModel.uiState.value.items.size)
    }

    @Test
    fun `AddItem assigns a unique id to each item`() = runTest {
        viewModel.handle(ItemsAction.AddItem("A"))
        viewModel.handle(ItemsAction.AddItem("B"))

        val ids = viewModel.uiState.value.items.map { it.id }
        assertEquals(ids.distinct().size, ids.size)
    }

    //endregion

    //region DeleteItem

    @Test
    fun `DeleteItem removes the item with the matching id`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(items = listOf(Item(id = "id-1", name = "ToDelete")))
        )

        viewModel.handle(ItemsAction.DeleteItem("id-1"))

        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun `DeleteItem sets deletionSuccess to true on success`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(items = listOf(Item(id = "id-1", name = "ToDelete")))
        )

        viewModel.handle(ItemsAction.DeleteItem("id-1"))

        assertTrue(viewModel.uiState.value.deletionSuccess)
    }

    @Test
    fun `DeleteItem with unknown id sets an error message`() = runTest {
        viewModel.handle(ItemsAction.DeleteItem("non-existent-id"))

        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `DeleteItem with unknown id does not change items list`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(items = listOf(Item(id = "id-1", name = "Safe")))
        )

        viewModel.handle(ItemsAction.DeleteItem("non-existent-id"))

        assertEquals(1, viewModel.uiState.value.items.size)
    }

    @Test
    fun `DeleteItem only removes the targeted item`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(
                items = listOf(
                    Item(id = "id-keep", name = "Keep"),
                    Item(id = "id-remove", name = "Remove")
                )
            )
        )

        viewModel.handle(ItemsAction.DeleteItem("id-remove"))

        val remaining = viewModel.uiState.value.items
        assertEquals(1, remaining.size)
        assertEquals("Keep", remaining.first().name)
    }

    //endregion

    //region ConsumeError

    @Test
    fun `ConsumeError clears the error message`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(errorMessage = "Some error")
        )

        viewModel.handle(ItemsAction.ConsumeError)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    //endregion

    //region ConsumeDeletionSuccess

    @Test
    fun `ConsumeDeletionSuccess resets deletionSuccess to false`() = runTest {
        viewModel = ItemsMviViewModel(
            initialState = ItemsUiState(deletionSuccess = true)
        )

        viewModel.handle(ItemsAction.ConsumeDeletionSuccess)

        assertFalse(viewModel.uiState.value.deletionSuccess)
    }

    //endregion
}