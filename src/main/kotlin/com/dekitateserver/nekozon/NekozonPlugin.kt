package com.dekitateserver.nekozon

import com.dekitateserver.nekolib.util.error
import com.dekitateserver.nekolib.util.setCommand
import com.dekitateserver.nekozon.command.NekozonCommand
import com.dekitateserver.nekozon.controller.NekozonController
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class NekozonPlugin : JavaPlugin(), Nekozon {

    lateinit var economy: Economy

    lateinit var nekozonController: NekozonController

    override fun onEnable() {
        economy = server.servicesManager.getRegistration(Economy::class.java)?.provider ?: let {
            logger.error("Failed to hook Vault")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        nekozonController = NekozonController(this)

        setCommand(NekozonCommand(this))

        logger.info("Enabled")
    }

    override fun onDisable() {
        nekozonController.cancelJob()

        logger.info("Disabled")
    }
}