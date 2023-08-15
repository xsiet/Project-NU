package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.avatar.AvatarDataManager
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.team.TeamAbility
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import io.github.xsiet.projectnu.utils.sendPacket
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageByEntityEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val server = plugin.server
    private val avatarManager get() = plugin.avatarManager
    private val teamAbilityManager get() = plugin.teamAbilityManager
    @EventHandler private fun EntityDamageByEntityEvent.on() {
        var damager = damager
        if (damager is Arrow) {
            val shooter = damager.shooter
            if (shooter is Entity) damager = shooter
        }
        if (damager is Player) {
            val uuid = damager.uniqueId
            if (teamAbilityManager.skillDurationMap.containsKey(uuid)) {
                if (TeamDataManager.getData(PlayerDataManager.getData(uuid).teamUUID).ability == TeamAbility.FOUR_LEAF_CLOVER) {
                    if ((1..100).random() <= 40) {
                        damage *= 2
                        damager.sendActionBarWithSound(text("두 배 공격을 성공하였습니다!", NamedTextColor.GREEN))
                    }
                }
            }
        }
        if (entity is Player && damager is Player && ConfigData.isFarmingTime) isCancelled = true
        else if (!ConfigData.isFarmingTime) avatarManager.apply {
            if (checkInteractionEntity(entity) && damager is Player) {
                val uuid = getAvatarUUID(entity)
                AvatarDataManager.getData(uuid).apply {
                    if (!isDead) {
                        isCancelled = false
                        if ((health - damage) < 0) health = 0.0
                        else health -= damage
                        if (health == 0.0) killAvatar(uuid, damager.uniqueId)
                        damage = 0.0
                        server.sendPacket(ClientboundHurtAnimationPacket(entityIdMap[uuid]!!, damage.toFloat()))
                    }
                }
            }
        }
    }
}