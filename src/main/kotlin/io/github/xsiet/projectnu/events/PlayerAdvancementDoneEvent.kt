package io.github.xsiet.projectnu.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class PlayerAdvancementDoneEvent: Listener {
    @EventHandler private fun PlayerAdvancementDoneEvent.on() { message(null) }
}