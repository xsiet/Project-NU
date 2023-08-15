package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.discord.utils.Mention
import io.github.xsiet.projectnu.manager.team.color
import io.github.xsiet.projectnu.manager.team.textColor
import io.github.xsiet.projectnu.utils.getRandomLocation
import io.github.xsiet.projectnu.utils.playBellSound
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.dv8tion.jda.api.EmbedBuilder
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val discordManager get() = plugin.discordManager
    private val channelManager get() = plugin.channelManager
    @EventHandler private fun BlockPlaceEvent.on() {
        player.apply {
            if (blockPlaced.type == Material.BEACON) {
                TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID).apply {
                    if (coreLocation == null) {
                        if (blockPlaced.y > 62) {
                            sendActionBarWithSound(text("팀 코어는 Y좌표 62이하에 설치해야 합니다!", NamedTextColor.RED))
                            isCancelled = true
                        }
                        else {
                            for (x: Int in (blockPlaced.x - 1)..(blockPlaced.x + 1)) {
                                for (z: Int in (blockPlaced.z - 1)..(blockPlaced.z + 1)) Location(
                                    blockPlaced.world,
                                    x.toDouble(),
                                    (blockPlaced.y - 1).toDouble(),
                                    z.toDouble()
                                ).block.type = Material.IRON_BLOCK
                            }
                            for (y: Int in (blockPlaced.y + 1)..319) Location(
                                blockPlaced.world,
                                blockPlaced.x.toDouble(),
                                y.toDouble(),
                                blockPlaced.z.toDouble()
                            ).block.type = Material.GLASS
                            if (location.block.x == blockPlaced.x && location.block.z == blockPlaced.z) {
                                teleport(location.getRandomLocation(5.0))
                            }
                            coreLocation = block.location
                            discordManager.getTextChannel(ConfigData.discordAnnouncementChannelId).sendMessage(
                                "@everyone"
                            ).addEmbeds(EmbedBuilder().apply {
                                setColor(ability.color)
                                setDescription("${Mention.role(discordRoleId)} 팀의 팀 코어가 설치되었습니다!")
                            }.build()).queue()
                            server.onlinePlayers.forEach {
                                it.playBellSound()
                                it.sendMessage(channelManager.getInGameMessageStructure(
                                    ChannelType.ALL,
                                    text(name, ability.textColor)
                                        .append(text(" 팀의 팀 코어가 설치되었습니다!", NamedTextColor.WHITE))
                                ))
                            }
                        }
                    }
                    else {
                        sendActionBarWithSound(text("팀 코어가 이미 설치되었습니다!", NamedTextColor.RED))
                        isCancelled = true
                    }
                }
            }
        }
    }
}