package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.item.CustomItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class EntityToggleGlideEvent: Listener {
    @EventHandler private fun EntityToggleGlideEvent.on() {
        if (entity is Player) {
            val inventory = (entity as Player).inventory
            if (inventory.getItem(EquipmentSlot.CHEST) == CustomItem.NO_RECYCLABLE_ELYTRA) {
                if (isGliding) entity.apply { velocity = location.direction.setY(1) }
                else inventory.setItem(EquipmentSlot.CHEST, ItemStack(Material.AIR))
            }
        }
    }
}