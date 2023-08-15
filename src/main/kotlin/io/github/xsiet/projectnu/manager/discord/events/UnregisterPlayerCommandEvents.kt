package io.github.xsiet.projectnu.manager.discord.events

import io.github.xsiet.projectnu.manager.discord.utils.embeds.errorEmbed
import io.github.xsiet.projectnu.manager.discord.utils.embeds.successEmbed
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun unregisterPlayerCommandInteractionEvent(event: SlashCommandInteractionEvent) {
    event.apply {
        val id = user.id
        if (PlayerDataManager.hasData(id)) {
            val data = PlayerDataManager.getData(id)
            if (data.hasTeam) replyEmbeds(errorEmbed("팀에 등록되어 플레이어 데이터 등록을 취소할 수 없습니다!").build()).queue()
            else {
                data.delete()
                replyEmbeds(successEmbed("플레이어 데이터 등록이 취소되었습니다!").build()).queue()
            }
        }
        else replyEmbeds(errorEmbed("플레이어 데이터가 등록되지 않았습니다!").build()).queue()
    }
}