package io.github.xsiet.projectnu.data.team

import io.github.xsiet.projectnu.manager.team.TeamAbility
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object TeamDataManager {
    private val dataPath = File("plugins/Project-NU/Teams").apply { if (!exists()) mkdir() }
    val uuids get() = ArrayList<UUID>().apply {
        dataPath.listFiles()?.forEach { add(UUID.fromString(it.name.replace(".yml", ""))) }
    }
    val availableAbilities get() = ArrayList<TeamAbility>().apply {
        TeamAbility.entries.forEach { add(it) }
        uuids.forEach { remove(getData(it).ability) }
    }
    fun isAvailableName(name: String): Boolean {
        var isAvailableName = true
        uuids.forEach { if (getData(it).name == name) isAvailableName = false }
        return isAvailableName
    }
    fun getData(uuid: UUID) = TeamData(dataPath, uuid)
}