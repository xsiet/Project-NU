package io.github.xsiet.projectnu.manager.discord.events

import io.github.xsiet.projectnu.data.avatar.AvatarDataManager
import io.github.xsiet.projectnu.manager.avatar.AvatarManager
import io.github.xsiet.projectnu.manager.discord.utils.Mention
import io.github.xsiet.projectnu.manager.discord.utils.embeds.errorEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.loadingEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.normalEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.successEmbed
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.manager.team.asTeamAbility
import io.github.xsiet.projectnu.manager.team.asTeamAbilityString
import io.github.xsiet.projectnu.manager.team.color
import io.github.xsiet.projectnu.utils.getRandomLocation
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.awt.Color
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

fun registerTeamCommandAutoCompleteInteractionEvent(event: CommandAutoCompleteInteractionEvent) {
    event.apply {
        if (focusedOption.name == "팀_능력") {
            replyChoices(Stream.of(*ArrayList<String>().apply {
                TeamDataManager.availableAbilities.forEach { add(it.asTeamAbilityString) }
            }.toArray(arrayOf<String>()))
                .filter { word: String -> word.startsWith(event.focusedOption.value) }
                .map { word: String? -> Command.Choice(word!!, word) }
                .collect(Collectors.toList())
            ).queue()
        }
    }
}
fun registerTeamCommandInteractionEvent(event: SlashCommandInteractionEvent, avatarManager: AvatarManager) {
    event.apply {
        val guild = guild!!
        val optionName = getOption("팀_이름")!!.asString.apply {
            if (!TeamDataManager.isAvailableName(this)) return hook.editOriginalEmbeds(
                errorEmbed(
                "입력하신 팀 이름은 이미 다른 플레이어가 사용 중입니다!\n다른 팀 이름으로 다시 시도해 주세요!"
            ).build()).queue()
            if (length > 8) return hook.editOriginalEmbeds(
                errorEmbed(
                "입력하신 팀 이름이 너무 깁니다!\n8자 이하로 다시 시도해 주세요!"
            ).build()).queue()
        }
        val optionAbility = getOption("팀_능력")!!.asString.asTeamAbility.apply {
            if (this == null) return replyEmbeds(
                errorEmbed(
                "입력하신 팀 능력은 존재하지 않습니다!\n확인 후 다시 시도해 주세요!"
            ).build()).queue()
        }
        val members = ArrayList<Member>().apply {
            arrayListOf("팀장", "팀원_1", "팀원_2", "팀원_3").forEach {
                val member = getOption(it)?.asMember
                if (member != null) add(member)
            }
        }
        for (index: Int in 0..< members.size) {
            val id = members[index].id
            if (!PlayerDataManager.hasData(id)) return replyEmbeds(
                errorEmbed(
                "${Mention.user(id)}님의 데이터가 등록되지 않았습니다!\n데이터 등록 후 다시 시도해 주세요!"
            ).build()).queue()
            PlayerDataManager.getData(id).apply {
                if (hasTeam) return replyEmbeds(
                    errorEmbed(
                    "${Mention.user(id)}님은 이미 다른 팀에 등록되어 있습니다!\n다른 플레이어로 다시 시도해 주세요!"
                ).build()).queue()
            }
        }
        val leaderUUID = PlayerDataManager.getData(members[0].id).uuid
        replyEmbeds(loadingEmbed("팀 데이터 등록 중...").build()).queue()
        TeamDataManager.getData(leaderUUID).apply {
            name = optionName
            ability = optionAbility!!
            guild.createRole().apply {
                setColor(ability.color)
                setName("[ 🚩┃$name ]")
                setHoisted(true)
            }.queue { role ->
                guild.createCategory(role.name).apply {
                    addRolePermissionOverride(
                        guild.publicRole.idLong,
                        null,
                        EnumSet.of(
                            Permission.VIEW_CHANNEL
                        )
                    )
                    addRolePermissionOverride(
                        role.idLong,
                        EnumSet.of(
                            Permission.VIEW_CHANNEL,
                            Permission.MESSAGE_ADD_REACTION,
                            Permission.MESSAGE_SEND,
                            Permission.MESSAGE_TTS,
                            Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_MENTION_EVERYONE,
                            Permission.MESSAGE_EXT_EMOJI,
                            Permission.USE_APPLICATION_COMMANDS,
                            Permission.MESSAGE_EXT_STICKER,
                            Permission.MESSAGE_ATTACH_VOICE_MESSAGE,
                            Permission.CREATE_PUBLIC_THREADS,
                            Permission.MESSAGE_SEND_IN_THREADS,
                            Permission.VOICE_STREAM,
                            Permission.VOICE_CONNECT,
                            Permission.VOICE_SPEAK,
                            Permission.VOICE_USE_VAD,
                            Permission.VOICE_START_ACTIVITIES,
                            Permission.VOICE_USE_SOUNDBOARD,
                            Permission.VOICE_USE_EXTERNAL_SOUNDS
                        ),
                        null
                    )
                }.queue { category ->
                    guild.createTextChannel("「💬」팀-채팅", category).queue { textChannel ->
                        textChannel.manager.sync(category).queue {
                            guild.createVoiceChannel("「🔊」팀-음성-채팅", category).queue { voiceChannel ->
                                voiceChannel.manager.sync(category).queue {
                                    discordRoleId = role.id
                                    discordCategoryId = category.id
                                    discordTextChannelId = textChannel.id
                                    discordVoiceChannelId = voiceChannel.id
                                    spawnLocation = Bukkit.getWorlds()[0].getRandomLocation()
                                    memberUUIDs = ArrayList<UUID>().apply {
                                        members.forEach { member ->
                                            val data = PlayerDataManager.getData(member.id)
                                            val nickname = data.nickname
                                            val uuid = data.uuid.apply { add(this) }
                                            guild.addRoleToMember(member, role).queue {
                                                var serverNickname = nickname
                                                if (uuid == leaderUUID) serverNickname = "[ 팀장 ] $serverNickname"
                                                try { member.modifyNickname(serverNickname).queue() } catch (_: Exception) {}
                                            }
                                            avatarManager.createAvatar(AvatarDataManager.getData(uuid).apply {
                                                location = spawnLocation.getRandomLocation(5.0)
                                                name = nickname
                                                skinUUID = leaderUUID
                                                health = 20.0
                                                maxHealth = health
                                                isDead = false
                                                equipments = ArrayList<ItemStack>().apply {
                                                    for (int: Int in 0..4) add(ItemStack(Material.AIR))
                                                }
                                                hotBarItems = ArrayList<ItemStack>().apply {
                                                    for (int: Int in 0..8) add(ItemStack(Material.AIR))
                                                    if (uuid == leaderUUID) set(0, ItemStack(Material.BEACON))
                                                }
                                                inventoryItems = ArrayList<ItemStack>().apply {
                                                    for (int: Int in 0..26) add(ItemStack(Material.AIR))
                                                }
                                            })
                                        }
                                    }
                                    hook.editOriginalEmbeds(successEmbed("팀 데이터 등록이 완료되었습니다!").apply {
                                        addField("팀 이름", name, false)
                                        addField("팀 능력", ability.asTeamAbilityString, false)
                                        var membersValue = ""
                                        for (index: Int in 0..< members.size) {
                                            val userId = members[index].id
                                            membersValue = "$membersValue${Mention.user(userId)}"
                                            if (index != members.size - 1) membersValue = "${membersValue},\n"
                                        }
                                        addField("팀원 (총 ${members.size}명)", membersValue, false)
                                    }.build()).queue()
                                    val mention = Mention.role(role.id)
                                    textChannel.sendMessage(mention).addEmbeds(
                                        normalEmbed(
                                        Color.GREEN,
                                        "👋",
                                        "WELCOME!",
                                        "환영합니다! 이곳은 $mention 팀원에게만 보이는 팀 전용 채널입니다!\n팀과 관련된 서버 로그가 이 채널로 전송되니 잘 확인해 주세요!"
                                    ).build()).queue()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}