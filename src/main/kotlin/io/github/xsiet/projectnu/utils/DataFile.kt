package io.github.xsiet.projectnu.utils

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class DataFile(path: File, name: String) {
    val file = File(path, name).apply { if (!exists()) createNewFile() }
    val data = YamlConfiguration.loadConfiguration(file)
    fun setData(path: String, value: Any) {
        data.apply {
            set(path, value)
            save(file)
        }
    }
}