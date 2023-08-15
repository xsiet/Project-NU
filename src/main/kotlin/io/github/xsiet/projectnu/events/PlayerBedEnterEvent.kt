package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent

class PlayerBedEnterEvent: Listener {
    @EventHandler private fun PlayerBedEnterEvent.on() {
        isCancelled = true
        player.sendActionBarWithSound(text("이 서버에서는 잠을 잘 수 없습니다!", NamedTextColor.RED))
    }
}