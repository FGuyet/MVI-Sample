package com.fguyet.sample.mvi.feature.items.mvvm

import com.fguyet.sample.mvi.model.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ItemsMvvmViewModelTest {

    private lateinit var viewModel: ItemsMvvmViewModel

    @Before
    fun setUp() {
        viewModel = ItemsMvvmViewModel()
    }

    //region Initial state

    @Test
    fun `initial items list is empty`() {
        assertTrue(viewModel.items.value.isEmpty())
    }

    //endregion

    //region addItem

    @Test
    fun `addItem with valid name returns success`() {
        val result = viewModel.addItem("Widget")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `addItem with valid name adds item to the list`() {
        viewModel.addItem("Widget")

        assertEquals(1, viewModel.items.value.size)
        assertEquals("Widget", viewModel.items.value.first().name)
    }

    @Test
    fun `addItem trims whitespace before adding`() {
        viewModel.addItem("  Gadget  ")

        assertEquals("Gadget", viewModel.items.value.first().name)
    }

    @Test
    fun `addItem with blank name returns failure`() {
        val result = viewModel.addItem("   ")

        assertTrue(result.isFailure)
    }

    @Test
    fun `addItem with blank name does not add any item`() {
        viewModel.addItem("   ")

        assertTrue(viewModel.items.value.isEmpty())
    }

    @Test
    fun `addItem with empty string returns failure`() {
        val result = viewModel.addItem("")

        assertTrue(result.isFailure)
    }

    @Test
    fun `addItem failure contains meaningful message`() {
        val result = viewModel.addItem("")

        assertNotNull(result.exceptionOrNull()?.message)
    }

    @Test
    fun `addItem returns the created item on success`() {
        val result = viewModel.addItem("Alpha")

        assertEquals("Alpha", result.getOrNull()?.name)
    }

    @Test
    fun `addItem multiple times accumulates items`() {
        viewModel.addItem("A")
        viewModel.addItem("B")
        viewModel.addItem("C")

        assertEquals(3, viewModel.items.value.size)
    }

    @Test
    fun `addItem assigns unique id to each item`() {
        viewModel.addItem("A")
        viewModel.addItem("B")

        val ids = viewModel.items.value.map { it.id }
        assertEquals(ids.distinct().size, ids.size)
    }

    //endregion

    //region deleteItem

    @Test
    fun `deleteItem with existing id returns success`() {
        viewModel = ItemsMvvmViewModel(initialItems = listOf(Item(id = "id-1", name = "ToDelete")))

        val result = viewModel.deleteItem("id-1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteItem removes the item from the list`() {
        viewModel = ItemsMvvmViewModel(initialItems = listOf(Item(id = "id-1", name = "ToDelete")))

        viewModel.deleteItem("id-1")

        assertTrue(viewModel.items.value.isEmpty())
    }

    @Test
    fun `deleteItem returns the deleted id on success`() {
        viewModel = ItemsMvvmViewModel(initialItems = listOf(Item(id = "id-1", name = "ToDelete")))

        val result = viewModel.deleteItem("id-1")

        assertEquals("id-1", result.getOrNull())
    }

    @Test
    fun `deleteItem with unknown id returns failure`() {
        val result = viewModel.deleteItem("non-existent-id")

        assertTrue(result.isFailure)
    }

    @Test
    fun `deleteItem with unknown id does not change the list`() {
        viewModel = ItemsMvvmViewModel(initialItems = listOf(Item(id = "id-1", name = "Safe")))

        viewModel.deleteItem("non-existent-id")

        assertEquals(1, viewModel.items.value.size)
    }

    @Test
    fun `deleteItem failure contains meaningful message`() {
        val result = viewModel.deleteItem("ghost")

        assertNotNull(result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteItem only removes the targeted item`() {
        viewModel = ItemsMvvmViewModel(
            initialItems = listOf(
                Item(id = "id-keep", name = "Keep"),
                Item(id = "id-remove", name = "Remove")
            )
        )

        viewModel.deleteItem("id-remove")

        val remaining = viewModel.items.value
        assertEquals(1, remaining.size)
        assertEquals("Keep", remaining.first().name)
    }

    //endregion
}
