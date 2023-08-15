package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent

class EntityExplodeEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val teamCoreManager get() = plugin.teamCoreManager
    @EventHandler private fun EntityExplodeEvent.on() {
        blockList().forEach {
            if (teamCoreManager.checkTeamCoreMaintenanceBlock(it) || it.type == Material.BEACON) {
                location.createExplosion(0F)
                isCancelled = true
            }
        }
    }
}