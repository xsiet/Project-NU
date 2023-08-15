package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.item.CustomItem
import io.github.xsiet.projectnu.manager.shop.ShopManager
import io.github.xsiet.projectnu.utils.playBellSound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class InventoryClickEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val teamMenuManager get() = plugin.teamMenuManager
    @EventHandler private fun InventoryClickEvent.on() {
        (whoClicked as Player).apply {
            if (currentItem != null && clickedInventory != null) {
                val currentItem = currentItem!!
                val inventory = clickedInventory!!
                val teamUUID = PlayerDataManager.getData(uniqueId).teamUUID
                when (currentItem) {
                    CustomItem.LOCKED.SLOT -> {
                        isCancelled = true
                        return
                    }
                    CustomItem.MENU.AVATAR_REVIVAL -> {
                        playBellSound()
                        openInventory(teamMenuManager.getAvatarRevivalMenuInventory(teamUUID))
                        isCancelled = true
                        return
                    }
                    CustomItem.MENU.NUNIUM_SHOP -> {
                        playBellSound()
                        openInventory(teamMenuManager.getNuniumShopMenuInventory(teamUUID))
                        isCancelled = true
                        return
                    }
                }
                if (ShopManager.checkShopInventory(inventory)) {
                    ShopManager.buyItem(this, inventory, slot)
                    isCancelled = true
                }
                else if (inventory.type == InventoryType.PLAYER) {
                    if (slot == 38 && currentItem == CustomItem.NO_RECYCLABLE_ELYTRA) isCancelled = true
                }
            }
        }
    }
}