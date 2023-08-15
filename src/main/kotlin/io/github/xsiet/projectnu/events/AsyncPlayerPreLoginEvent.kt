package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.data.avatar.AvatarDataManager
import io.github.xsiet.projectnu.data.config.ConfigData
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class AsyncPlayerPreLoginEvent: Listener {
    @EventHandler private fun AsyncPlayerPreLoginEvent.on() {
        if (!ConfigData.isAccessible) disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            text(
                "관리자에 의해 모든 아바타 연결이 차단되어 있습니다!",
                NamedTextColor.RED
            )
        )
        else if (!AvatarDataManager.uuids.contains(uniqueId)) disallow(
            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            text(
                "아바타가 생성되지 않아 아바타에 연결할 수 없습니다!",
                NamedTextColor.RED
            )
        )
        else {
            AvatarDataManager.getData(uniqueId).apply {
                if (isDead) disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    text("아바타 사망으로 인해 아바타에 연결할 수 없습니다!\n\n", NamedTextColor.RED)
                        .append(text("$remainingTimeoutTimeAsString 후 ", NamedTextColor.WHITE))
                        .append(text("아바타가 자동 부활됩니다!\n(또는 팀원에게 즉시 부활을 요청해 보세요!)", NamedTextColor.GRAY))
                )
            }
        }
    }
}