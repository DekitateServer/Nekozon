package com.dekitateserver.nekozon.command

import com.dekitateserver.nekolib.command.BaseCommand
import com.dekitateserver.nekozon.NekozonPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NekozonCommand(plugin: NekozonPlugin) : BaseCommand("nekozon") {

    private val controller = plugin.nekozonController

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            return false
        }

        controller.openNekozon(sender)

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ) = emptyList<String>()

}