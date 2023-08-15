package io.github.xsiet.projectnu.events

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

class EntitySpawnEvent: Listener {
    @EventHandler private fun EntitySpawnEvent.on() { if (entityType == EntityType.PHANTOM) isCancelled = true }
}