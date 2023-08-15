package io.github.xsiet.projectnu.manager.team

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.utils.addPotionEffect
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.potion.PotionEffectType
import java.awt.Color
import java.util.UUID

enum class TeamAbility {
    CHEETAH,
    TURTLE,
    WILD_BOAR,
    ROE_DEER,
    FOUR_LEAF_CLOVER
}
val TeamAbility.color: Color get() = when (this) {
    TeamAbility.CHEETAH -> Color.YELLOW
    TeamAbility.TURTLE -> Color.CYAN
    TeamAbility.WILD_BOAR -> Color.RED
    TeamAbility.ROE_DEER -> Color.ORANGE
    TeamAbility.FOUR_LEAF_CLOVER -> Color.GREEN
}
val TeamAbility.textColor: TextColor get() = when (this) {
    TeamAbility.CHEETAH -> NamedTextColor.YELLOW
    TeamAbility.TURTLE -> NamedTextColor.AQUA
    TeamAbility.WILD_BOAR -> NamedTextColor.RED
    TeamAbility.ROE_DEER -> NamedTextColor.GOLD
    TeamAbility.FOUR_LEAF_CLOVER -> NamedTextColor.GREEN
}
val TeamAbility.asTeamAbilityString get() = when (this) {
    TeamAbility.CHEETAH -> "치타"
    TeamAbility.TURTLE -> "거북이"
    TeamAbility.WILD_BOAR -> "멧돼지"
    TeamAbility.ROE_DEER -> "노루"
    TeamAbility.FOUR_LEAF_CLOVER -> "네잎클로버"
}
val String.asTeamAbility get() = when (this) {
    "CHEETAH", "치타" -> TeamAbility.CHEETAH
    "TURTLE", "거북이" -> TeamAbility.TURTLE
    "WILD_BOAR", "멧돼지" -> TeamAbility.WILD_BOAR
    "ROE_DEER", "노루" -> TeamAbility.ROE_DEER
    "FOUR_LEAF_CLOVER", "네잎클로버" -> TeamAbility.FOUR_LEAF_CLOVER
    else -> null
}
class TeamAbilityManager(
    plugin: ProjectNUPlugin
) {
    private val server = plugin.server
    val skillDurationMap = LinkedHashMap<UUID, Int>()
    val absoptionEffectCoolDownMap = LinkedHashMap<UUID, Int>()
    fun enableSkill(uuid: UUID) {
        when (TeamDataManager.getData(PlayerDataManager.getData(uuid).teamUUID).ability) {
            TeamAbility.CHEETAH, TeamAbility.ROE_DEER -> TODO()
            TeamAbility.TURTLE, TeamAbility.WILD_BOAR, TeamAbility.FOUR_LEAF_CLOVER -> {
                skillDurationMap[uuid] = 1 * 60
            }
        }
    }
    init {
        server.scheduler.runTaskTimer(plugin, Runnable {
            skillDurationMap.forEach {
                val uuid = it.key
                val player = server.getPlayer(uuid)
                if (it.value == 0) {
                    if (skillDurationMap.containsKey(uuid)) skillDurationMap.remove(uuid)
                    server.getPlayer(uuid)?.sendActionBarWithSound(text("스킬이 비활성화되었습니다!", NamedTextColor.RED))
                }
                else {
                    val duration = skillDurationMap[uuid]!! - 1
                    skillDurationMap[uuid] = duration
                    player?.sendActionBar(
                        text("스킬 비활성화까지 ", NamedTextColor.RED)
                            .append(text("${duration / 60}분 ${duration % 60}초 ".replace("0분 ", ""), NamedTextColor.WHITE))
                            .append(text("남았습니다!", NamedTextColor.RED))
                    )
                }
            }
            absoptionEffectCoolDownMap.forEach {
                val uuid = it.key
                if (it.value == 0) absoptionEffectCoolDownMap.remove(uuid)
                else absoptionEffectCoolDownMap[uuid] = absoptionEffectCoolDownMap[uuid]!! - 1
            }
            server.onlinePlayers.forEach {
                it.apply {
                    val duration = 2
                    when (TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID).ability) {
                        TeamAbility.CHEETAH -> addPotionEffect(PotionEffectType.SPEED, duration, 1)
                        TeamAbility.TURTLE -> if (!absoptionEffectCoolDownMap.containsKey(uniqueId)) {
                            addPotionEffect(PotionEffectType.ABSORPTION, 10 * 60, 1)
                        }
                        TeamAbility.WILD_BOAR -> {
                            var amplifier = 1
                            if (skillDurationMap.containsKey(uniqueId)) amplifier = 8
                            addPotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier)
                        }
                        TeamAbility.ROE_DEER -> addPotionEffect(PotionEffectType.JUMP, duration, 2)
                        TeamAbility.FOUR_LEAF_CLOVER -> addPotionEffect(PotionEffectType.LUCK, duration, 1)
                    }
                }
            }
        }, 0, 20L)
    }
}