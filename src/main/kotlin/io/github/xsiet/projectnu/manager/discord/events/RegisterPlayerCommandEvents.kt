package io.github.xsiet.projectnu.manager.discord.events

import com.google.gson.Gson
import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.discord.utils.embeds.errorEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.loadingEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.successEmbed
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun registerPlayerCommandInteractionEvent(event: SlashCommandInteractionEvent) {
    event.apply {
        val id = user.id
        val minecraftUserName = getOption("마인크래프트_닉네임")!!.asString
        if (PlayerDataManager.hasData(id)) return replyEmbeds(errorEmbed("플레이어 데이터가 이미 등록되었습니다!").build()).queue()
        replyEmbeds(loadingEmbed("마인크래프트 프로필 조회 중...").build()).queue()
        val uuid: UUID?
        try {
            val spec = "https://api.mojang.com/users/profiles/minecraft/${minecraftUserName}"
            var line: String?
            val response = StringBuilder()
            (URL(spec).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                BufferedReader(InputStreamReader(inputStream)).apply {
                    while (readLine().also { line = it } != null) { response.append(line) }
                    close()
                }
                disconnect()
            }
            data class PlayerData(val id: String, val name: String)
            uuid = UUID.fromString(
                StringBuilder(Gson().fromJson(response.toString(), PlayerData::class.java).id).apply {
                    insert(8, "-")
                    insert(13, "-")
                    insert(18, "-")
                    insert(23, "-")
                }.toString()
            )
        }
        catch (_: Exception) {
            return hook.editOriginalEmbeds(
                errorEmbed(
                "마인크래프트 프로필을 조회하지 못했습니다!\n마인크래프트 닉네임 확인 후 다시 시도해 주세요!"
            ).build()).queue()
        }
        if (PlayerDataManager.hasData(uuid)) return hook.editOriginalEmbeds(
            errorEmbed(
            "해당 마인크래프트 프로필은 이미 다른 플레이어가 사용 중입니다!\n다른 마인크래프트 닉네임으로 다시 시도해 주세요!"
        ).build()).queue()
        val optionNickname = getOption("서버_닉네임")!!.asString.apply {
            if (!PlayerDataManager.isAvailableNickname(this)) return hook.editOriginalEmbeds(
                errorEmbed(
                "입력하신 서버 닉네임은 이미 다른 플레이어가 사용 중입니다!\n다른 서버 닉네임으로 다시 시도해 주세요!"
            ).build()).queue()
            if (contains(" ")) return hook.editOriginalEmbeds(
                errorEmbed(
                "서버 닉네임에는 띄어쓰기를 사용할 수 없습니다!\n다른 서버 닉네임으로 다시 시도해 주세요!"
            ).build()).queue()
            if (length > 8) return hook.editOriginalEmbeds(
                errorEmbed(
                "입력하신 서버 닉네임이 너무 깁니다!\n8자 이하인 다른 서버 닉네임으로 다시 시도해 주세요!"
            ).build()).queue()
        }
        PlayerDataManager.getData(uuid).apply {
            nickname = optionNickname
            discordId = id
            channelType = ChannelType.ALL
            try { member!!.modifyNickname(nickname).queue() } catch (_: Exception) {}
            hook.editOriginalEmbeds(
                successEmbed(
                "플레이어 데이터 등록이 완료되었습니다!"
            ).apply {
                setThumbnail("https://cravatar.eu/helmhead/${minecraftUserName}/600")
                addField("마인크래프트 닉네임", minecraftUserName, false)
                addField("서버 닉네임", nickname, false)
            }.build()).queue()
        }
    }
}