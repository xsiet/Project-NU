package io.github.xsiet.projectnu.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent

class BlockPistonExtendEvent: Listener {
    @EventHandler private fun BlockPistonExtendEvent.on() { isCancelled = true }
}