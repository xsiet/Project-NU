package io.github.xsiet.projectnu.data.config

import io.github.xsiet.projectnu.utils.DataFile
import java.io.File

object ConfigData {
    private val dataFile = DataFile(File("plugins/Project-NU"), "config.yml")
    private val data get() = dataFile.data
    private fun setData(path: String, value: Any) = dataFile.setData(path, value)
    var discordGuildId: String
        get() = data.getString("discordGuildId")!!
        set(value) = setData("discordGuildId", value)
    var discordAnnouncementChannelId: String
        get() = data.getString("discordAnnouncementChannelId")!!
        set(value) = setData("discordAnnouncementChannelId", value)
    var discordPlazaChannelId: String
        get() = data.getString("discordPlazaChannelId")!!
        set(value) = setData("discordPlazaChannelId", value)
    var isAccessible: Boolean
        get() = data.getBoolean("isAccessible")
        set(value) = setData("isAccessible", value)
    var isFarmingTime: Boolean
        get() = data.getBoolean("isFarmingTime")
        set(value) = setData("isFarmingTime", value)
}