package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.team.textColor
import io.github.xsiet.projectnu.utils.sendPacket
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import org.bukkit.craftbukkit.v1_20_R1.scoreboard.CraftScoreboard
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val server = plugin.server
    private val avatarManager get() = plugin.avatarManager
    private val itemManager get() = plugin.itemManager
    @EventHandler private fun PlayerJoinEvent.on() {
        joinMessage(null)
        val data = PlayerDataManager.getData(player.uniqueId)
        TeamDataManager.getData(data.teamUUID).apply {
            player.playerListName(text("[ $name ] ", ability.textColor).append(text(data.nickname, NamedTextColor.WHITE)))
        }
        TeamDataManager.uuids.forEach { teamUUID ->
            TeamDataManager.getData(teamUUID).apply {
                PlayerTeam((server.scoreboardManager.mainScoreboard as CraftScoreboard).handle, uuid.toString()).apply {
                    if (uuid != data.teamUUID) nameTagVisibility = Team.Visibility.NEVER
                    player.sendPacket(arrayListOf(
                        ClientboundSetPlayerTeamPacket.createRemovePacket(this),
                        ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(this, true),
                        ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(
                            this,
                            ArrayList<String>().apply {
                                memberUUIDs.forEach { add(PlayerDataManager.getData(it).nickname) }
                            },
                            ClientboundSetPlayerTeamPacket.Action.ADD
                        )
                    ))
                }
            }
        }
        avatarManager.apply {
            player.apply {
                connectAvatar(this)
                sendPackets(this)
            }
        }
        itemManager.register(player.uniqueId)
    }
}