package com.dekitateserver.nekozon.data

import com.dekitateserver.nekozon.NekozonPlugin
import com.dekitateserver.nekozon.data.model.Item
import org.bukkit.Material

class ItemRepository(plugin: NekozonPlugin) {

    fun getItemList(): List<Item> {
        return Material.values().map {
            Item(1, it, it.name, 100.0)
        }
    }

}