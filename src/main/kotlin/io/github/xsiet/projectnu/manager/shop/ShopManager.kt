package io.github.xsiet.projectnu.manager.shop

import io.github.xsiet.projectnu.item.CustomItem
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ShopManager {
    private val saleItemMap = LinkedHashMap<Inventory, LinkedHashMap<Int, ArrayList<Any>>>()
    fun checkShopInventory(inventory: Inventory) = saleItemMap.keys.contains(inventory)
    fun registerInventory(inventory: Inventory) { saleItemMap[inventory] = LinkedHashMap() }
    fun setItem(inventory: Inventory, slot: Int, item: ItemStack, needItem: ItemStack, task: Runnable) {
        val displayItem = item.clone().apply {
            var keyStart = "item."
            if (needItem.type.isBlock) keyStart = "block."
            val needItemLore = arrayListOf(
                text(""),
                text("필요한 아이템", NamedTextColor.AQUA)
                    .append(text(": ", NamedTextColor.WHITE))
                    .append(translatable("${keyStart}minecraft.${needItem.type.toString().lowercase()}", NamedTextColor.YELLOW))
                    .append(text(" ${needItem.amount}개", NamedTextColor.YELLOW))
                    .decoration(TextDecoration.ITALIC, false)
            )
            itemMeta = itemMeta.apply {
                if (lore() == null) lore(needItemLore)
                else lore(lore()!!.apply { addAll(needItemLore) })
            }
        }
        saleItemMap[inventory]!![slot] = arrayListOf(displayItem, needItem, task)
        inventory.setItem(slot, displayItem)
        for (index: Int in (slot + 1)..<inventory.size) inventory.setItem(index, CustomItem.LOCKED.SLOT)
    }
    fun setItem(inventory: Inventory, slot: Int, item: ItemStack, needItem: ItemStack) {
        setItem(inventory, slot, item, needItem) {}
        saleItemMap[inventory]!![slot]!![2] = item
    }
    fun buyItem(player: Player, inventory: Inventory, slot: Int) {
        saleItemMap[inventory]!![slot]!!.apply {
            player.closeInventory()
            val needItem = get(1) as ItemStack
            if (player.inventory.contains(needItem.type, needItem.amount)) {
                player.inventory.removeItem(needItem)
                if (get(2) is Runnable) (get(2) as Runnable).run()
                else {
                    player.inventory.addItem(get(2) as ItemStack).values.forEach {
                        player.world.dropItem(player.location, it)
                    }
                    player.sendActionBarWithSound(text("성공적으로 아이템을 구입하였습니다!", NamedTextColor.GREEN))
                }
            }
            else player.sendActionBarWithSound(text("해당 작업을 하기 위한 아이템이 부족합니다!", NamedTextColor.RED))
        }
    }
}