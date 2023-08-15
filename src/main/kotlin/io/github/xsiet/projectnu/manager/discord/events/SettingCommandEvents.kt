package io.github.xsiet.projectnu.manager.discord.events

import io.github.xsiet.projectnu.data.config.ConfigData
import io.github.xsiet.projectnu.manager.discord.utils.Mention
import io.github.xsiet.projectnu.manager.discord.utils.embeds.errorEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.successEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun settingCommandInteractionEvent(event: SlashCommandInteractionEvent) {
    event.apply {
        val guild = guild!!
        when (subcommandGroup) {
            "디스코드" -> when (subcommandName) {
                "연동서버" -> {
                    if (ConfigData.discordGuildId == "") {
                        ConfigData.discordGuildId = guild.id
                        replyEmbeds(successEmbed("이 서버를 마인크래프트 서버와 연동하였습니다!").build()).queue()
                    }
                    else replyEmbeds(errorEmbed("이미 마인크래프트 서버와 연동되었습니다!").build()).queue()
                }
                "공지채널" -> {
                    val channelId = getOption("채널")!!.asChannel.id
                    if (ConfigData.discordAnnouncementChannelId == channelId) replyEmbeds(
                        errorEmbed(
                        "이미 공지 채널이 ${Mention.channel(channelId)}채널로 설정되어 있습니다!"
                    ).build()).queue()
                    else {
                        if (guild.getTextChannelById(channelId) == null) replyEmbeds(
                            errorEmbed(
                            "해당 채널을 찾을 수 없습니다!"
                        ).build()).queue()
                        else {
                            ConfigData.discordAnnouncementChannelId = channelId
                            replyEmbeds(
                                successEmbed(
                                "공지 채널이 ${Mention.channel(channelId)}채널로 설정되었습니다!"
                            ).build()).queue()
                        }
                    }
                }
                "광장채널" -> {
                    val channelId = getOption("채널")!!.asChannel.id
                    if (ConfigData.discordPlazaChannelId == channelId) replyEmbeds(
                        errorEmbed(
                        "이미 광장 채널이 ${Mention.channel(channelId)}채널로 설정되어 있습니다!"
                    ).build()).queue()
                    else {
                        if (guild.getTextChannelById(channelId) == null) replyEmbeds(
                            errorEmbed(
                            "해당 채널을 찾을 수 없습니다!"
                        ).build()).queue()
                        else {
                            ConfigData.discordPlazaChannelId = channelId
                            replyEmbeds(
                                successEmbed(
                                "광장 채널이 ${Mention.channel(channelId)}채널로 설정되었습니다!"
                            ).build()).queue()
                        }
                    }
                }
            }
            "서버접속" -> when (subcommandName) {
                "허용" -> {
                    if (ConfigData.isAccessible) replyEmbeds(
                        errorEmbed(
                        "이미 모든 플레이어의 서버 접속이 허용되어 있습니다!"
                    ).build()).queue()
                    else {
                        ConfigData.isAccessible = true
                        replyEmbeds(successEmbed("모든 플레이어의 서버 접속이 허용되었습니다!").build()).queue()
                    }
                }
                "차단" -> {
                    if (ConfigData.isAccessible) {
                        ConfigData.isAccessible = false
                        replyEmbeds(successEmbed("모든 플레이어의 서버 접속이 차단되었습니다!").build()).queue()
                    }
                    else replyEmbeds(errorEmbed("이미 모든 플레이어의 서버 접속이 차단되어 있습니다!").build()).queue()
                }
            }
            "파밍시간" -> when (subcommandName) {
                "활성화" -> {
                    if (ConfigData.isFarmingTime) replyEmbeds(errorEmbed("이미 파밍 시간이 활성화되어 있습니다!").build()).queue()
                    else {
                        ConfigData.isFarmingTime = true
                        replyEmbeds(successEmbed("파밍 시간이 활성화되었습니다!").build()).queue()
                    }
                }
                "비활성화" -> {
                    if (ConfigData.isFarmingTime) {
                        ConfigData.isFarmingTime = false
                        replyEmbeds(successEmbed("파밍 시간이 비활성화되었습니다!").build()).queue()
                    }
                    else replyEmbeds(errorEmbed("이미 파밍 시간이 비활성화되어 있습니다!").build()).queue()
                }
            }
        }
    }
}