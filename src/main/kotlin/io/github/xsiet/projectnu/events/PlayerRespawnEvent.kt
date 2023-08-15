package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.utils.getRandomLocation
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerRespawnEvent: Listener {
    @EventHandler private fun PlayerRespawnEvent.on() {
        player.apply {
            if (bedSpawnLocation == null) {
                TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID).apply {
                    respawnLocation = if (coreLocation == null) spawnLocation.getRandomLocation(5.0)
                    else coreLocation!!.getRandomLocation(5.0)
                }
            }
        }
    }
}