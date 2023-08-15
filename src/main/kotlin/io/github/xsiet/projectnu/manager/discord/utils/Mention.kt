package io.github.xsiet.projectnu.manager.discord.utils

object Mention {
    fun user(id: String) = "<@!${id}>"
    fun role(id: String) = "<@&${id}>"
    fun channel(id: String) = "<#${id}>"
}
