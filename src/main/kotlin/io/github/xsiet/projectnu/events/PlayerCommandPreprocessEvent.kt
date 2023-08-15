package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessEvent: Listener {
    private val allowCommands = arrayListOf("채널", "channel", "c")
    @EventHandler private fun PlayerCommandPreprocessEvent.on() {
        if (!player.isOp) {
            var cancel = true
            allowCommands.forEach {
                if (message.replace("/", "").startsWith(it)) cancel = false
            }
            isCancelled = cancel
            if (cancel) player.sendActionBarWithSound(text("해당 명령어를 사용할 수 없습니다!", NamedTextColor.RED))
        }
    }
}