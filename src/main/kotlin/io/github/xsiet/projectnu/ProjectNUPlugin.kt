package io.github.xsiet.projectnu

import io.github.xsiet.projectnu.manager.avatar.AvatarManager
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.manager.discord.DiscordManager
import io.github.xsiet.projectnu.events.*
import io.github.xsiet.projectnu.manager.channel.ChannelManager
import io.github.xsiet.projectnu.manager.item.ItemManager
import io.github.xsiet.projectnu.manager.team.TeamAbilityManager
import io.github.xsiet.projectnu.manager.team.TeamCoreManager
import io.github.xsiet.projectnu.manager.team.TeamMenuManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.File
import java.nio.charset.StandardCharsets

class ProjectNUPlugin: JavaPlugin(), PluginMessageListener {
    lateinit var avatarManager: AvatarManager
    lateinit var discordManager: DiscordManager
    lateinit var channelManager: ChannelManager
    lateinit var itemManager: ItemManager
    lateinit var teamAbilityManager: TeamAbilityManager
    lateinit var teamCoreManager: TeamCoreManager
    lateinit var teamMenuManager: TeamMenuManager
    override fun onEnable() {
        File(dataFolder.path).apply {
            if (!exists()) {
                mkdir()
                ConfigData.apply {
                    discordGuildId = ""
                    discordAnnouncementChannelId = ""
                    discordPlazaChannelId = ""
                    isAccessible = false
                    isFarmingTime = true
                }
            }
        }
        avatarManager = AvatarManager(this)
        discordManager = DiscordManager(this)
        channelManager = ChannelManager(this)
        itemManager = ItemManager(this)
        teamAbilityManager = TeamAbilityManager(this)
        teamCoreManager = TeamCoreManager(this)
        teamMenuManager = TeamMenuManager(this)
        arrayListOf(
            AsyncChatEvent(this),
            AsyncPlayerPreLoginEvent(),
            BlockBreakEvent(this),
            BlockPistonExtendEvent(),
            BlockPistonRetractEvent(),
            BlockPlaceEvent(this),
            EntityDamageByEntityEvent(this),
            EntityDamageEvent(this),
            EntityExplodeEvent(this),
            EntityMoveEvent(this),
            EntitySpawnEvent(),
            EntityToggleGlideEvent(),
            InventoryClickEvent(this),
            InventoryCloseEvent(this),
            PaperServerListPingEvent(),
            PlayerAdvancementDoneEvent(),
            PlayerBedEnterEvent(),
            PlayerChangedWorldEvent(this),
            PlayerCommandPreprocessEvent(),
            PlayerDeathEvent(this),
            PlayerInteractAtEntityEvent(this),
            PlayerInteractEvent(this),
            PlayerJoinEvent(this),
            PlayerQuitEvent(this),
            PlayerRespawnEvent()
        ).forEach { server.pluginManager.registerEvents(it, this) }
        server.messenger.registerIncomingPluginChannel(this, "minecraft:brand", this)
    }
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (!String(message, StandardCharsets.UTF_8).substring(1).startsWith("lunarclient")) player.kick(
            text(
                "이 서버는 Lunar Client를 통한 접속만이 허용됩니다!\n\nLunar Client로 서버 접속을 시도해 주세요!",
                NamedTextColor.RED
            )
        )
    }
    override fun onDisable() {
        server.onlinePlayers.forEach { avatarManager.disconnectAvatar(it) }
    }
}