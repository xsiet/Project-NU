package io.github.xsiet.projectnu.data.player

import io.github.xsiet.projectnu.manager.channel.ChannelType
import io.github.xsiet.projectnu.manager.channel.asChannelType
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.utils.DataFile
import java.io.File
import java.util.UUID

class PlayerData(
    dataPath: File,
    val uuid: UUID
) {
    private val dataFile = DataFile(dataPath, "${uuid}.yml")
    private val data get() = dataFile.data
    private fun setData(path: String, value: Any) = dataFile.setData(path, value)
    var nickname: String
        get() = data.getString("nickname")!!
        set(value) = setData("nickname", value)
    var discordId: String
        get() = data.getString("discordId")!!
        set(value) = setData("discordId", value)
    var channelType: ChannelType
        get() = data.getString("channelType")!!.asChannelType!!
        set(value) = setData("channelType", value.toString())
    val hasTeam: Boolean get() {
        var hasTeam = false
        TeamDataManager.uuids.forEach { if (TeamDataManager.getData(it).memberUUIDs.contains(uuid)) hasTeam = true }
        return hasTeam
    }
    val teamUUID: UUID get() {
        var teamUUID: UUID? = null
        TeamDataManager.uuids.forEach { if (TeamDataManager.getData(it).memberUUIDs.contains(uuid)) teamUUID = it }
        return teamUUID!!
    }
    fun delete() = dataFile.file.delete()
}