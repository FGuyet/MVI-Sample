package com.fguyet.sample.mvi.factory

import com.fguyet.sample.mvi.model.Item
import java.util.UUID

class ItemFactory {
    fun create(name: String): Item = Item(
        id = UUID.randomUUID().toString(),
        name = name
    )
}