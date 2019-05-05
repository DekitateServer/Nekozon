package com.dekitateserver.nekozon.inventory

import com.dekitateserver.nekolib.inventory.InventoryGui
import com.dekitateserver.nekolib.inventory.itemMeta
import com.dekitateserver.nekolib.inventory.itemStack
import com.dekitateserver.nekolib.inventory.lore
import com.dekitateserver.nekozon.Nekozon
import com.dekitateserver.nekozon.controller.NekozonController
import com.dekitateserver.nekozon.data.model.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max

class ItemDetailInventoryGui(
    private val controller: NekozonController,
    private val item: Item,
    private val parentInventoryGui: InventoryGui
) : InventoryGui() {

    private companion object {
        const val LINE_SIZE = 9
        const val INVENTORY_SIZE = LINE_SIZE * 3
        const val BACK_ITEM_SLOT = INVENTORY_SIZE - 1
    }

    private val maxStackSize = ItemStack(item.material).maxStackSize
    private val itemSlotRange = if (maxStackSize == 1) 13..13 else 11..15

    private val inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Nekozon.TITLE).apply {
        for (slot in itemSlotRange) {
            setItem(slot, itemStack(item.material) {
                itemMeta {
                    amount = slot.itemAmount
                    setDisplayName("${item.name} x $amount")
                    lore("§7価格： §6${controller.formatCost(item.price * amount)}", "§eクリックで購入")
                }
            })
        }

        setItem(BACK_ITEM_SLOT, itemStack(Material.ARROW) {
            itemMeta { setDisplayName("§c§l戻る") }
        })
    }

    override fun getInventory() = inventory

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        when (val slot = event.rawSlot) {
            in itemSlotRange -> controller.buyItem(player, item, slot.itemAmount)
            BACK_ITEM_SLOT -> player.openParentGui()
        }
    }

    private val Int.itemAmount: Int
        get() {
            val index = this - itemSlotRange.first
            return max(1, maxStackSize / 4 * index)
        }

    private fun Player.openParentGui() = openInventory(parentInventoryGui.inventory)

}