package io.github.xsiet.projectnu.data.team

import io.github.xsiet.projectnu.manager.team.TeamAbility
import io.github.xsiet.projectnu.manager.team.asTeamAbility
import io.github.xsiet.projectnu.utils.DataFile
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

class TeamData(
    dataPath: File,
    val uuid: UUID
) {
    private val dataFile = DataFile(dataPath, "${uuid}.yml")
    private val data get() = dataFile.data
    private fun setData(path: String, value: Any) = dataFile.setData(path, value)
    var name: String
        get() = data.getString("name")!!
        set(value) = setData("name", value)
    var ability: TeamAbility
        get() = data.getString("ability")!!.asTeamAbility!!
        set(value) = setData("ability", value.toString())
    var discordRoleId: String
        get() = data.getString("discordRoleId")!!
        set(value) = setData("discordRoleId", value)
    var discordCategoryId: String
        get() = data.getString("discordCategoryId")!!
        set(value) = setData("discordCategoryId", value)
    var discordTextChannelId: String
        get() = data.getString("discordTextChannelId")!!
        set(value) = setData("discordTextChannelId", value)
    var discordVoiceChannelId: String
        get() = data.getString("discordVoiceChannelId")!!
        set(value) = setData("discordVoiceChannelId", value)
    var spawnLocation: Location
        get() = data.getLocation("spawnLocation")!!
        set(value) = setData("spawnLocation", value)
    var memberUUIDs: ArrayList<UUID>
        get() = ArrayList<UUID>().apply {
            data.getStringList("memberUUIDs").forEach { add(UUID.fromString(it)) }
        }
        set(value) = setData("memberUUIDs", ArrayList<String>().apply {
            value.forEach { add(it.toString()) }
        })
    var coreLocation: Location?
        get() = data.getLocation("coreLocation")
        set(value) { if (value != null) setData("coreLocation", value) }
    val onlineMembers get() = ArrayList<Player>().apply {
        Bukkit.getOnlinePlayers().forEach { if (memberUUIDs.contains(it.uniqueId)) add(it) }
    }
    fun delete() = dataFile.file.delete()
}