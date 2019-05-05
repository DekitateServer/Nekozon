package com.dekitateserver.nekozon.inventory

import com.dekitateserver.nekolib.inventory.InventoryGui
import com.dekitateserver.nekolib.inventory.itemMeta
import com.dekitateserver.nekolib.inventory.itemStack
import com.dekitateserver.nekolib.inventory.lore
import com.dekitateserver.nekolib.util.playCancelSound
import com.dekitateserver.nekolib.util.playClickSound
import com.dekitateserver.nekozon.Nekozon
import com.dekitateserver.nekozon.controller.NekozonController
import com.dekitateserver.nekozon.data.model.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import kotlin.math.min
import kotlin.properties.Delegates

class NekozonInventoryGui(
    private val controller: NekozonController,
    itemList: List<Item>
) : InventoryGui() {

    private companion object {
        const val LINE_SIZE = 9
        const val ITEM_SLOT_SIZE = LINE_SIZE * 4
        const val INVENTORY_SIZE = LINE_SIZE * 5
        const val EXIT_ITEM_SLOT = INVENTORY_SIZE - 1
        const val PAGE_SLOT = 36

        const val MAX_PAGE_SIZE = 7
    }

    private val chunkedDisplayItemList = itemList.map(::DisplayItem).chunked(ITEM_SLOT_SIZE)
    private val displayItemList = chunkedDisplayItemList.first().toMutableList()

    private val sumPage = min(chunkedDisplayItemList.size - 1, MAX_PAGE_SIZE)

    private val pageSlotRange = PAGE_SLOT..PAGE_SLOT + sumPage
    private val pageRange = 0..sumPage

    private var page: Int by Delegates.observable(0) { _, _, new ->
        displayItemList.apply {
            clear()
            addAll(chunkedDisplayItemList[new])
        }
    }

    private val baseInventory = Bukkit.createInventory(this, INVENTORY_SIZE, Nekozon.TITLE)

    override fun getInventory() = baseInventory.apply {
        clear()

        displayItemList.forEachIndexed { index, item ->
            setItem(index, item.itemStack)
        }

        createFooter()
    }

    override fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val itemRange = 0 until displayItemList.size

        when (val slot = event.rawSlot) {
            in itemRange -> controller.openItemDetail(player, displayItemList[slot].item, this)
            in pageSlotRange -> player.updatePage(slot - PAGE_SLOT)
            EXIT_ITEM_SLOT -> player.closeInventory()
        }
    }

    private fun Inventory.createFooter() {
        //page
        for (p in pageRange) {
            setItem(PAGE_SLOT + p, itemStack(if (p == page) Material.ORANGE_DYE else Material.GRAY_DYE) {
                itemMeta { setDisplayName("${p+1}ページ") }
            })
        }
        // page dummy
        for (p in sumPage + 1..MAX_PAGE_SIZE) {
            setItem(PAGE_SLOT + p, itemStack(Material.GRAY_STAINED_GLASS_PANE) {
                itemMeta { setDisplayName(" ") }
            })
        }

        // exit
        setItem(EXIT_ITEM_SLOT, itemStack(Material.ARROW) {
            itemMeta { setDisplayName("§c§l閉じる") }
        })
    }

    private fun Player.updatePage(newPage: Int) {
        if (newPage !in pageRange || newPage == page) {
            playCancelSound()
            return
        }

        page = newPage

        playClickSound()
        openInventory(this@NekozonInventoryGui.inventory)
    }


    private inner class DisplayItem(
        val item: Item
    ) {
        val itemStack = itemStack(item.material) {
            itemMeta {
                setDisplayName(item.name)
                lore("§7価格： §6${controller.formatCost(item.price)}", "§eクリックで選択")
            }
        }
    }

}