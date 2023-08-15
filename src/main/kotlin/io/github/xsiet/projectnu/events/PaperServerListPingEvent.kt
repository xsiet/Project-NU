package io.github.xsiet.projectnu.events

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PaperServerListPingEvent: Listener {
    @EventHandler private fun PaperServerListPingEvent.on() {
        motd(text("NUNUNUNUNUNUNUNUNUNUNUNUNUNU", NamedTextColor.YELLOW))
        setHidePlayers(true)
        numPlayers = 0
        maxPlayers = 0
    }
}