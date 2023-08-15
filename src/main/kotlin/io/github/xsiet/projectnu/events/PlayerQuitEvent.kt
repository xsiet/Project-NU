package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    @EventHandler private fun PlayerQuitEvent.on() {
        quitMessage(null)
        avatarManager.disconnectAvatar(player)
    }
}