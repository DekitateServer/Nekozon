package com.dekitateserver.nekozon.controller

import com.dekitateserver.nekolib.controller.BaseController
import com.dekitateserver.nekolib.coroutines.Bukkit
import com.dekitateserver.nekolib.inventory.InventoryGui
import com.dekitateserver.nekolib.util.playCancelSound
import com.dekitateserver.nekolib.util.playClickSound
import com.dekitateserver.nekolib.util.playNyanSound
import com.dekitateserver.nekozon.Nekozon
import com.dekitateserver.nekozon.NekozonPlugin
import com.dekitateserver.nekozon.data.ItemRepository
import com.dekitateserver.nekozon.data.model.Item
import com.dekitateserver.nekozon.inventory.ItemDetailInventoryGui
import com.dekitateserver.nekozon.inventory.NekozonInventoryGui
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class NekozonController(plugin: NekozonPlugin) : BaseController() {

    private val economy = plugin.economy
    private val repository = ItemRepository(plugin)

    fun openNekozon(player: Player) {
        launch {
            val itemList = repository.getItemList()
            val inventoryGui = NekozonInventoryGui(this@NekozonController, itemList)

            withContext(Dispatchers.Bukkit) {
                player.playClickSound()
                player.openInventory(inventoryGui.inventory)
            }
        }
    }

    fun openItemDetail(player: Player, item: Item, parent: InventoryGui) {
        launch {
            val inventoryGui = ItemDetailInventoryGui(this@NekozonController, item, parent)

            withContext(Dispatchers.Bukkit) {
                player.playClickSound()
                player.openInventory(inventoryGui.inventory)
            }
        }
    }

    fun buyItem(player: Player, item: Item, amount: Int) {
        launch {
            val price = item.price * amount

            if (!economy.has(player, price)) {
                player.playCancelSound()
                player.sendWarnMessage("十分な所持金がありません.")
                return@launch
            }

            val response = economy.withdrawPlayer(player, price)
            if (!response.transactionSuccess()) {
                player.playCancelSound()
                player.sendErrorMessage("購入処理に失敗しました.")
                return@launch
            }

            player.sendMessage("${Nekozon.PREFIX}§a${item.name}§aを${amount}個購入しました.")

            val itemStack = item.asItemStack(amount)
            withContext(Dispatchers.Bukkit) {
                player.playNyanSound()
                player.giveItemStack(itemStack)
            }
        }
    }

    fun formatCost(price: Double): String = economy.format(price)

    private fun Player.giveItemStack(itemStack: ItemStack) {
        if (inventory.firstEmpty() >= 0) {
            inventory.addItem(itemStack)
        } else {
            world.dropItem(location, itemStack)
            sendWarnMessage("インベントリに空きがないためドロップしました.")
        }
    }

    private fun Player.sendWarnMessage(message: String) = sendMessage("${Nekozon.PREFIX}§e$message")

    private fun Player.sendErrorMessage(message: String) = sendMessage("${Nekozon.PREFIX}§c$message")
}