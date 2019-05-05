package com.dekitateserver.nekozon.data.model

import com.dekitateserver.nekolib.inventory.itemStack
import org.bukkit.Material

data class Item(
    val id: Int,
    val material: Material,
    val name: String,
    val price: Double
) {
    fun asItemStack(amount: Int) = itemStack(material) { setAmount(amount) }
}