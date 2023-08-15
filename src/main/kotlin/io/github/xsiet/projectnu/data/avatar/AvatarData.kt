package io.github.xsiet.projectnu.data.avatar

import com.google.gson.Gson
import io.github.xsiet.projectnu.utils.DataFile
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AvatarData(
    dataPath: File,
    inventoryDataPath: File,
    val uuid: UUID
) {
    private val gson = Gson()
    private val dataFile = DataFile(dataPath, "${uuid}.yml")
    private val inventoryDataFile = DataFile(inventoryDataPath, "${uuid}.yml")
    private val data get() = dataFile.data
    private val inventoryData get() = inventoryDataFile.data
    private fun setData(path: String, value: Any) = dataFile.setData(path, value)
    private fun setInventoryData(path: String, value: Any) = inventoryDataFile.setData(path, value)
    var location: Location
        get() = data.getLocation("location")!!
        set(value) = setData("location", value)
    var name: String
        get() = data.getString("name")!!
        set(value) = setData("name", value)
    var skinUUID: UUID
        get() = UUID.fromString(data.getString("skinUUID"))
        set(value) = setData("skinUUID", value.toString())
    var health: Double
        get() = data.getDouble("health")
        set(value) = setData("health", value)
    var maxHealth: Double
        get() = data.getDouble("maxHealth")
        set(value) = setData("maxHealth", value)
    var foodLevel: Int
        get() = data.getInt("foodLevel")
        set(value) = setData("foodLevel", value)
    var isDead: Boolean
        get() = data.getBoolean("isDead")
        set(value) = setData("isDead", value)
    var timeout: Long
        get() = data.getLong("timeout")
        set(value) = setData("timeout", value)
    @Suppress("UNCHECKED_CAST")
    private fun List<String>.toItemStackArrayList(): ArrayList<ItemStack> = ArrayList<ItemStack>().apply {
        this@toItemStackArrayList.forEach {
            if (it.startsWith("{\"v\":")) add(ItemStack.deserialize(gson.fromJson(it, Map::class.java) as Map<String, Any>))
            else {
                if (it == "empty") add(ItemStack(Material.AIR))
                else add(ItemStack.deserializeBytes(Base64.getDecoder().decode(it)))
            }
        }
    }
    private fun ArrayList<ItemStack>.toStringArrayList(): ArrayList<String> = ArrayList<String>().apply {
        this@toStringArrayList.forEach {
            if (it.type.isAir) add("empty")
            else add(Base64.getEncoder().encodeToString(it.serializeAsBytes()))
        }
    }
    var equipments: ArrayList<ItemStack>
        get() = inventoryData.getStringList("equipments").toItemStackArrayList()
        set(value) = setInventoryData("equipments", value.toStringArrayList())
    var hotBarItems: ArrayList<ItemStack>
        get() = inventoryData.getStringList("hotBarItems").toItemStackArrayList()
        set(value) = setInventoryData("hotBarItems", value.toStringArrayList())
    var inventoryItems: ArrayList<ItemStack>
        get() = inventoryData.getStringList("inventoryItems").toItemStackArrayList()
        set(value) = setInventoryData("inventoryItems", value.toStringArrayList())
    val remainingTimeoutTime: ArrayList<Int> get() {
        var seconds = ((timeout - System.currentTimeMillis()) / 1000).toInt()
        var minutes = ((seconds - (seconds % 60)) / 60)
        seconds %= 60
        val hours = ((minutes - (minutes % 60)) / 60)
        minutes %= 60
        return arrayListOf(hours, minutes, seconds)
    }
    val remainingTimeoutTimeAsString: String get() {
        val time = remainingTimeoutTime
        return "${time[0]}시간 ${time[1]}분 ${time[2]}초".replace("0시간 ", "").replace("0분 ", "")
    }
    fun delete() {
        dataFile.file.delete()
        inventoryDataFile.file.delete()
    }
}