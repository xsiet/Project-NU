package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.utils.playSound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import java.util.UUID

class PlayerInteractAtEntityEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    private val coolDownList = ArrayList<UUID>()
    @EventHandler private fun PlayerInteractAtEntityEvent.on() {
        player.apply {
            if (!ConfigData.isFarmingTime && !coolDownList.contains(uniqueId)) avatarManager.apply {
                if (checkInteractionEntity(rightClicked)) {
                    coolDownList.add(uniqueId)
                    swingMainHand()
                    playSound("block.chest.open", 0.4F)
                    openInventory(inventoryMap[getAvatarUUID(rightClicked)]!!)
                    isCancelled = true
                    server.scheduler.runTaskLater(plugin, Runnable { coolDownList.remove(uniqueId) }, 1)
                }
            }
        }
    }
}