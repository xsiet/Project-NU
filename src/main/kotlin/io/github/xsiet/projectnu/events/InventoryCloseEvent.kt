package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.utils.playSound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    @EventHandler private fun InventoryCloseEvent.on() {
        avatarManager.apply {
            if (avatarManager.inventoryMap.containsValue(inventory)) (player as Player).playSound("block.chest.close", 0.4F)
        }
    }
}