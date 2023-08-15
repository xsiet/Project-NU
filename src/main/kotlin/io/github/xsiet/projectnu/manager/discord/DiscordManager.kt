package io.github.xsiet.projectnu.manager.discord

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.manager.discord.commands.registerCommand
import io.github.xsiet.projectnu.manager.discord.commands.settingCommand
import io.github.xsiet.projectnu.manager.discord.utils.embeds.errorEmbed
import io.github.xsiet.projectnu.manager.discord.utils.isAdministrator
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.discord.events.*
import io.github.xsiet.projectnu.manager.team.textColor
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

class DiscordManager(
    private val plugin: ProjectNUPlugin
): ListenerAdapter() {
    private val server = plugin.server
    private val avatarManager get() = plugin.avatarManager
    private val channelManager get() = plugin.channelManager
    private val jda = JDABuilder.createLight(
        "MTEzODA4MDQ4MjMxMjA2OTE5MQ.Gyvpgv.4RCBnmZhDj-Y_nT2ViiM5Z-Akm_8FssoaPogVw"
    ).apply {
        enableIntents(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.MESSAGE_CONTENT
        )
        setMemberCachePolicy(MemberCachePolicy.ALL)
        setChunkingFilter(ChunkingFilter.ALL)
        setStatus(OnlineStatus.OFFLINE)
        addEventListeners(this@DiscordManager)
    }.build()
    val guild get() = jda.getGuildById(ConfigData.discordGuildId)!!
    fun getTextChannel(id: String) = guild.getTextChannelById(id)!!
    fun getMemberAvatarUrl(id: String): String? {
        val member = guild.getMemberById(id)!!
        return member.avatarUrl ?: member.user.avatarUrl
    }
    override fun onReady(event: ReadyEvent) {
        jda.updateCommands().addCommands(
            settingCommand,
            registerCommand
        ).queue()
    }
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        event.apply {
            when (name) {
                "등록" -> {
                    when (subcommandName) {
                        "팀" -> return registerTeamCommandAutoCompleteInteractionEvent(this)
                    }
                }
            }
        }
    }
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        event.apply {
            val member = member!!
            fun needPermission() = replyEmbeds(errorEmbed("이 명령어를 사용할 권한이 부족합니다!").build()).queue()
            when (name) {
                "설정" -> {
                    return if (member.isAdministrator) settingCommandInteractionEvent(this)
                    else needPermission()
                }
                "등록" -> {
                    when (subcommandGroup) {
                        "취소" -> {
                            when (subcommandName) {
                                "플레이어" -> return unregisterPlayerCommandInteractionEvent(this)
                            }
                        }
                    }
                    when (subcommandName) {
                        "플레이어" -> return registerPlayerCommandInteractionEvent(this)
                        "팀" -> {
                            return if (member.isAdministrator) registerTeamCommandInteractionEvent(this, avatarManager)
                            else needPermission()
                        }
                    }
                }
            }
        }
    }
    override fun onMessageReceived(event: MessageReceivedEvent) {
        event.apply {
            val id = author.id
            val content = message.contentDisplay
            if (PlayerDataManager.hasData(id) && content != "") {
                PlayerDataManager.getData(id).apply {
                    if (hasTeam) {
                        val message = text("$nickname : $content", NamedTextColor.WHITE)
                        TeamDataManager.getData(teamUUID).apply {
                            when (channel.id) {
                                ConfigData.discordPlazaChannelId -> server.onlinePlayers.forEach {
                                    it.sendMessage(channelManager.getInGameMessageStructure(
                                        ChannelType.ALL,
                                        text("[ $name ] ", ability.textColor).append(message)
                                    ))
                                }
                                discordTextChannelId -> onlineMembers.forEach {
                                    it.sendMessage(channelManager.getInGameMessageStructure(ChannelType.TEAM, message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}