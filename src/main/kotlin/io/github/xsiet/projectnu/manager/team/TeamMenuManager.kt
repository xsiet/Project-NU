package io.github.xsiet.projectnu.manager.team

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.avatar.AvatarDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.item.CustomItem
import io.github.xsiet.projectnu.manager.shop.ShopManager
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

class TeamMenuManager(
    plugin: ProjectNUPlugin
) {
    private val server = plugin.server
    private val nuniumShopMenuInventoryMap = LinkedHashMap<TeamAbility, Inventory>()
    private val avatarRevivalMenuInventoryMap = LinkedHashMap<UUID, Inventory>()
    fun getAvatarRevivalMenuInventory(uuid: UUID) = avatarRevivalMenuInventoryMap[uuid]!!
    fun getNuniumShopMenuInventory(uuid: UUID) = nuniumShopMenuInventoryMap[TeamDataManager.getData(uuid).ability]!!
    init {
        nuniumShopMenuInventoryMap.apply {
            fun getInventory() = server.createInventory(null, 9, text("구매할 아이템을 선택하세요!")).apply {
                ShopManager.registerInventory(this)
                ShopManager.setItem(
                    this,
                    0,
                    CustomItem.NO_RECYCLABLE_ELYTRA,
                    ItemStack(Material.LAPIS_BLOCK, 64)
                )
                ShopManager.setItem(
                    this,
                    1,
                    CustomItem.TELEPORT.SPAWN,
                    ItemStack(Material.LAPIS_BLOCK, 64)
                )
                ShopManager.setItem(
                    this,
                    2,
                    CustomItem.TELEPORT.CORE,
                    ItemStack(Material.LAPIS_BLOCK, 64)
                )
            }
            set(TeamAbility.CHEETAH, getInventory().apply {
                ShopManager.setItem(
                    this,
                    3,
                    CustomItem.SKILL.CHEETAH,
                    ItemStack(Material.LAPIS_BLOCK, 64 * 3)
                )
            })
            set(TeamAbility.TURTLE, getInventory().apply {
                ShopManager.setItem(
                    this,
                    3,
                    CustomItem.SKILL.TURTLE,
                    ItemStack(Material.LAPIS_BLOCK, 64 * 3)
                )
            })
            set(TeamAbility.WILD_BOAR, getInventory().apply {
                ShopManager.setItem(
                    this,
                    3,
                    CustomItem.SKILL.WILD_BOAR,
                    ItemStack(Material.LAPIS_BLOCK, 64 * 3)
                )
            })
            set(TeamAbility.ROE_DEER, getInventory().apply {
                ShopManager.setItem(
                    this,
                    3,
                    CustomItem.SKILL.ROE_DEER,
                    ItemStack(Material.LAPIS_BLOCK, 64 * 3)
                )
            })
            set(TeamAbility.FOUR_LEAF_CLOVER, getInventory().apply {
                ShopManager.setItem(
                    this,
                    3,
                    CustomItem.SKILL.FOUR_LEAF_CLOVER,
                    ItemStack(Material.LAPIS_BLOCK, 64 * 3)
                )
            })
        }
        server.scheduler.runTaskTimer(plugin, Runnable {
            TeamDataManager.uuids.forEach { teamUUID ->
                if (!avatarRevivalMenuInventoryMap.containsKey(teamUUID)) {
                    avatarRevivalMenuInventoryMap[teamUUID] = server.createInventory(
                        null,
                        27,
                        text("부활시킬 아바타를 선택하세요!")
                    ).apply { ShopManager.registerInventory(this) }
                }
                val inventory = avatarRevivalMenuInventoryMap[teamUUID]!!
                TeamDataManager.getData(teamUUID).apply {
                    var nextSlot = 0
                    memberUUIDs.forEach {
                        if (server.getPlayer(it) == null) {
                            AvatarDataManager.getData(it).apply {
                                if (isDead) {
                                    fun getNeedItemAmount(): Int {
                                        val time = remainingTimeoutTime
                                        return (((time[0] * 60) + time[1]) / 6) + 2
                                    }
                                    ShopManager.setItem(inventory, nextSlot, ItemStack(Material.NETHER_STAR).apply {
                                        itemMeta = itemMeta.apply {
                                            displayName(
                                                text("${name}님의 아바타", NamedTextColor.LIGHT_PURPLE)
                                                    .decoration(TextDecoration.ITALIC, false)
                                            )
                                            lore(arrayListOf(
                                                text("자동 부활까지 남은 시간", NamedTextColor.GOLD)
                                                    .append(text(": ", NamedTextColor.WHITE))
                                                    .append(text(remainingTimeoutTimeAsString, NamedTextColor.RED))
                                                    .decoration(TextDecoration.ITALIC, false)
                                            ))
                                        }
                                    }, ItemStack(Material.LAPIS_BLOCK, getNeedItemAmount())) { timeout = System.currentTimeMillis() }
                                    nextSlot ++
                                }
                            }
                        }
                    }
                    for (index: Int in nextSlot..<inventory.size) inventory.setItem(index, CustomItem.LOCKED.SLOT)
                }
            }
        }, 0, 1)
    }
}