package io.github.xsiet.projectnu.data.player

import java.io.File
import java.util.*

object PlayerDataManager {
    private val dataPath = File("plugins/Project-NU/Players").apply { if (!exists()) mkdir() }
    private val uuids get() = ArrayList<UUID>().apply {
        dataPath.listFiles()?.forEach { add(UUID.fromString(it.name.replace(".yml", ""))) }
    }
    fun hasData(uuid: UUID) = File(dataPath, "${uuid}.yml").exists()
    fun hasData(discordId: String): Boolean {
        var hasData = false
        uuids.forEach { if (getData(it).discordId == discordId) hasData = true }
        return hasData
    }
    fun isAvailableNickname(nickname: String): Boolean {
        var isAvailableNickname = true
        uuids.forEach { if (getData(it).nickname == nickname) isAvailableNickname = false }
        return isAvailableNickname
    }
    fun getData(uuid: UUID) = PlayerData(dataPath, uuid)
    fun getData(discordId: String): PlayerData {
        var playerData: PlayerData? = null
        uuids.forEach {
            val data = getData(it)
            if (data.discordId == discordId) playerData = data
        }
        return playerData!!
    }
}