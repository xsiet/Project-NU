package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    @EventHandler private fun PlayerDeathEvent.on() {
        deathMessage(null)
        player.apply {
            if (!ConfigData.isFarmingTime) {
                if (killer == null) avatarManager.killAvatar(uniqueId, null)
                else avatarManager.killAvatar(uniqueId, killer!!.uniqueId)
            }
        }
    }
}