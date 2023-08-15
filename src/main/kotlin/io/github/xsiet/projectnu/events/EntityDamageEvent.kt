package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.team.TeamAbility
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val avatarManager get() = plugin.avatarManager
    private val teamAbilityManager get() = plugin.teamAbilityManager
    @EventHandler private fun EntityDamageEvent.on() {
        if (entity is Player) {
            val uuid = entity.uniqueId
            if (TeamDataManager.getData(PlayerDataManager.getData(uuid).teamUUID).ability == TeamAbility.TURTLE) {
                teamAbilityManager.apply {
                    if (skillDurationMap.containsKey(uuid)) damage *= 0.7
                    if (!absoptionEffectCoolDownMap.containsKey(uuid)) absoptionEffectCoolDownMap[uuid] = 10 * 60
                }
            }
        }
        else if (avatarManager.checkInteractionEntity(entity)) isCancelled = true
    }
}