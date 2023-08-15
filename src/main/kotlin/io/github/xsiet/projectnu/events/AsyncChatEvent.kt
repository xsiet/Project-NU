package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.team.color
import io.github.xsiet.projectnu.manager.team.textColor
import io.papermc.paper.event.player.AsyncChatEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.awt.Color

class AsyncChatEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val server = plugin.server
    private val channelManager get() = plugin.channelManager
    private val discordManager get() = plugin.discordManager
    @EventHandler private fun AsyncChatEvent.on() {
        isCancelled = true
        PlayerDataManager.getData(player.uniqueId).apply {
            val message = "$nickname : ${PlainTextComponentSerializer.plainText().serialize(message())}"
            val messageComponent = text(message, NamedTextColor.WHITE)
            val avatarUrl = discordManager.getMemberAvatarUrl(discordId)
            TeamDataManager.getData(teamUUID).apply {
                when (channelType) {
                    ChannelType.ALL -> {
                        discordManager.getTextChannel(ConfigData.discordPlazaChannelId).sendMessageEmbeds(EmbedBuilder().apply {
                            setColor(ability.color)
                            setAuthor("[ $name ] $message", null, avatarUrl)
                        }.build()).queue()
                        server.onlinePlayers.forEach {
                            it.sendMessage(channelManager.getInGameMessageStructure(
                                channelType,
                                text("[ $name ] ", ability.textColor).append(messageComponent)
                            ))
                        }
                    }
                    ChannelType.TEAM -> {
                        discordManager.getTextChannel(discordTextChannelId).sendMessageEmbeds(EmbedBuilder().apply {
                            setColor(Color.WHITE)
                            setAuthor(message, null, avatarUrl)
                        }.build()).queue()
                        onlineMembers.forEach {
                            it.sendMessage(channelManager.getInGameMessageStructure(channelType, messageComponent))
                        }
                    }
                }
            }
        }
    }
}