package io.github.xsiet.projectnu.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonRetractEvent

class BlockPistonRetractEvent: Listener {
    @EventHandler private fun BlockPistonRetractEvent.on() { isCancelled = true }
}