package io.github.xsiet.projectnu.manager.item

import io.github.xsiet.projectnu.ProjectNUPlugin
import org.bukkit.inventory.ItemStack
import java.util.UUID

class ItemManager(
    plugin: ProjectNUPlugin
) {
    private val server = plugin.server
    private val itemCoolDownMap = LinkedHashMap<UUID, LinkedHashMap<ItemStack, Int>>()
    fun register(uuid: UUID) { if (!itemCoolDownMap.containsKey(uuid)) itemCoolDownMap[uuid] = LinkedHashMap() }
    fun getItemCoolDown(uuid: UUID, item: ItemStack) = itemCoolDownMap[uuid]!![item.clone().apply { amount = 1 }]
    fun setItemCoolDown(uuid: UUID, item: ItemStack, coolDown: Int) = itemCoolDownMap[uuid]!!.set(item.clone().apply { amount = 1 }, coolDown)
    init {
        server.scheduler.runTaskTimer(plugin, Runnable {
            itemCoolDownMap.values.forEach { map ->
                map.forEach {
                    val uuid = it.key
                    if (it.value == 0) map.remove(uuid)
                    else map[uuid] = map[uuid]!! - 1
                }
            }
        }, 0, 20L)
    }
}