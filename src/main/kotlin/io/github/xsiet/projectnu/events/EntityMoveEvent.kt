package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EntityMoveEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    @EventHandler private fun EntityMoveEvent.on() { if (avatarManager.checkInteractionEntity(entity)) isCancelled = true }
}