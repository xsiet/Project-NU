package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.discord.utils.Mention
import io.github.xsiet.projectnu.manager.team.color
import io.github.xsiet.projectnu.manager.team.textColor
import io.github.xsiet.projectnu.utils.playBellSound
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.dv8tion.jda.api.EmbedBuilder
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val teamCoreManager get() = plugin.teamCoreManager
    private val discordManager get() = plugin.discordManager
    private val channelManager get() = plugin.channelManager
    @EventHandler private fun BlockBreakEvent.on() {
        player.apply {
            if (teamCoreManager.checkTeamCoreMaintenanceBlock(block)) {
                sendActionBarWithSound(text("해당 블록은 파괴할 수 없습니다!", NamedTextColor.RED))
                isCancelled = true
            }
            else TeamDataManager.uuids.forEach { teamUUID ->
                TeamDataManager.getData(teamUUID).apply {
                    if (coreLocation != null) {
                        val core = coreLocation!!.block
                        if (block.location == core.location) {
                            if (memberUUIDs.contains(uniqueId)) {
                                sendActionBarWithSound(text("자신이 속한 팀의 코어는 파괴할 수 없습니다!", NamedTextColor.RED))
                                isCancelled = true
                            }
                            else {
                                val breaker = PlayerDataManager.getData(uniqueId)
                                val newTeam = TeamDataManager.getData(breaker.teamUUID)
                                newTeam.memberUUIDs = newTeam.memberUUIDs.apply { addAll(memberUUIDs) }
                                memberUUIDs.forEach {
                                    PlayerDataManager.getData(it).apply {
                                        discordManager.guild.apply {
                                            getMemberById(discordId)!!.apply {
                                                addRoleToMember(this, getRoleById(newTeam.discordRoleId)!!).queue()
                                                try { modifyNickname(nickname).queue() } catch (_: Exception) {}
                                            }
                                        }
                                    }
                                }
                                discordManager.getTextChannel(ConfigData.discordAnnouncementChannelId).sendMessage(
                                    "@everyone"
                                ).addEmbeds(EmbedBuilder().apply {
                                    setColor(newTeam.ability.color)
                                    setDescription("@[ 🚩┃$name ] 팀이 ${Mention.role(newTeam.discordRoleId)} 팀에 병합되었습니다!")
                                }.build()).queue()
                                server.onlinePlayers.forEach {
                                    it.apply {
                                        playBellSound()
                                        sendMessage(channelManager.getInGameMessageStructure(
                                            ChannelType.ALL,
                                            text(name, ability.textColor)
                                                .append(text(" 팀이 ", NamedTextColor.WHITE))
                                                .append(text(newTeam.name, newTeam.ability.textColor))
                                                .append(text(" 팀에 병합되었습니다!", NamedTextColor.WHITE))
                                        ))
                                    }
                                }
                                onlineMembers.forEach {
                                    it.kick(text(
                                        "팀 병합으로 인해 아바타와 연결이 끊어졌습니다!\n\n다시 접속해 주세요!",
                                        NamedTextColor.RED
                                    ))
                                }
                                discordManager.guild.apply {
                                    getRoleById(discordRoleId)!!.delete().queue()
                                    getCategoryById(discordCategoryId)!!.delete().queue()
                                    getTextChannelById(discordTextChannelId)!!.delete().queue()
                                    getVoiceChannelById(discordVoiceChannelId)!!.delete().queue()
                                }
                                delete()
                                isDropItems = false
                            }
                        }
                    }
                }
            }
        }
    }
}