package io.github.xsiet.projectnu.manager.discord.utils

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import java.util.EnumSet

val Member.isAdministrator get() = hasPermission(EnumSet.of(Permission.ADMINISTRATOR))