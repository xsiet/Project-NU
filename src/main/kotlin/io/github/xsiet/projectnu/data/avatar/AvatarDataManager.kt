package io.github.xsiet.projectnu.data.avatar

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object AvatarDataManager {
    private val dataPath = File("plugins/Project-NU/Avatars").apply { if (!exists()) mkdir() }
    private val inventoryDataPath = File("${dataPath.path}-Inventory").apply { if (!exists()) mkdir() }
    val uuids get() = ArrayList<UUID>().apply {
        dataPath.listFiles()?.forEach { add(UUID.fromString(it.name.replace(".yml", ""))) }
    }
    fun getData(uuid: UUID) = AvatarData(dataPath, inventoryDataPath, uuid)
}