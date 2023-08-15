package io.github.xsiet.projectnu.manager.discord.utils.embeds

import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

fun normalEmbed(color: Color, emoji: String, title: String, description: String) = EmbedBuilder().apply {
    setColor(color)
    setTitle("[ $emoji ] $title")
    setDescription(description)
}