package io.github.xsiet.projectnu.manager.discord.utils.embeds

import java.awt.Color

fun errorEmbed(description: String) = normalEmbed(Color.RED, "🔴", "ERROR!", description)