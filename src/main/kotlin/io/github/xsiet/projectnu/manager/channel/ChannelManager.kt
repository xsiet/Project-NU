package io.github.xsiet.projectnu.manager.channel

import io.github.monun.kommand.kommand
import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerData
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.discord.utils.Mention
import io.github.xsiet.projectnu.utils.playBellSound
import net.dv8tion.jda.api.EmbedBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import java.awt.Color

enum class ChannelType {
    ALL,
    TEAM
}
val ChannelType.asChannelTypeString get() = when (this) {
    ChannelType.ALL -> "전체"
    ChannelType.TEAM -> "팀"
}
val String.asChannelType get() = when (this) {
    "ALL" -> ChannelType.ALL
    "TEAM" -> ChannelType.TEAM
    else -> null
}
class ChannelManager(
    private val plugin: ProjectNUPlugin
) {
    private val discordManager get() = plugin.discordManager
    fun getInGameMessageStructure(channelType: ChannelType, message: Component): TextComponent {
        return text(channelType.asChannelTypeString, NamedTextColor.WHITE)
            .append(text(" | ", NamedTextColor.DARK_GRAY))
            .append(message)
    }
    init {
        plugin.kommand {
            register("채널", "channel", "c") {
                requires { isPlayer }
                fun setChannelType(player: Player, newChannelType: ChannelType) {
                    PlayerDataManager.getData(player.uniqueId).apply {
                        if (channelType == newChannelType) player.sendMessage(
                            text("이미 채팅 채널이 ", NamedTextColor.RED)
                                .append(text(channelType.asChannelTypeString, NamedTextColor.WHITE))
                                .append(text(" 채널로 설정되어 있습니다!", NamedTextColor.RED))
                        )
                        else {
                            channelType = newChannelType
                            player.sendMessage(
                                text("채팅 채널을 ", NamedTextColor.GREEN)
                                    .append(text(channelType.asChannelTypeString, NamedTextColor.WHITE))
                                    .append(text(" 채널로 설정하였습니다!", NamedTextColor.GREEN))
                            )
                        }
                    }
                }
                then("전체") {
                    executes { setChannelType(player, ChannelType.ALL) }
                }
                then("팀") {
                    executes { setChannelType(player, ChannelType.TEAM) }
                }
            }
        }
    }
    fun sendPlayerLogMessageToTeamChannel(data: PlayerData, color: Color, textColor: TextColor, message: String, mention: Boolean) {
        data.apply {
            val embed = EmbedBuilder().apply {
                setColor(color)
                setAuthor(message, null, discordManager.getMemberAvatarUrl(discordId))
            }.build()
            TeamDataManager.getData(teamUUID).apply {
                discordManager.getTextChannel(discordTextChannelId).apply {
                    if (mention) sendMessage(Mention.role(discordRoleId)).addEmbeds(embed).queue()
                    else sendMessageEmbeds(embed).queue()
                }
                onlineMembers.forEach {
                    it.apply {
                        playBellSound()
                        sendMessage(getInGameMessageStructure(ChannelType.TEAM, text(message, textColor)))
                    }
                }
            }
        }
    }
}