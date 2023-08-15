package io.github.xsiet.projectnu.events

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.item.CustomItem
import io.github.xsiet.projectnu.manager.team.TeamAbility
import io.github.xsiet.projectnu.manager.team.asTeamAbilityString
import io.github.xsiet.projectnu.utils.getRandomLocation
import io.github.xsiet.projectnu.utils.playBellSound
import io.github.xsiet.projectnu.utils.sendActionBarWithSound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class PlayerInteractEvent(
    private val plugin: ProjectNUPlugin
): Listener {
    private val server = plugin.server
    private val itemManager get() = plugin.itemManager
    private val teamAbilityManager get() = plugin.teamAbilityManager
    private val coreMenuInventory = server.createInventory(
        null,
        9,
        text("팀 코어 메뉴")
    ).apply {
        arrayListOf(0, 1, 3, 4, 5, 7, 8).forEach { setItem(it, CustomItem.LOCKED.SLOT) }
        setItem(2, CustomItem.MENU.AVATAR_REVIVAL)
        setItem(6, CustomItem.MENU.NUNIUM_SHOP)
    }
    @EventHandler private fun PlayerInteractEvent.on() {
        player.apply {
            if (action.isRightClick) {
                if (item != null) {
                    val item = item!!
                    val chestplates = arrayListOf(
                        Material.LEATHER_CHESTPLATE,
                        Material.CHAINMAIL_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.GOLDEN_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.NETHERITE_CHESTPLATE,
                        Material.ELYTRA
                    )
                    if (chestplates.contains(item.type) && inventory.getItem(EquipmentSlot.CHEST) == CustomItem.NO_RECYCLABLE_ELYTRA) {
                        isCancelled = true
                    }
                    else {
                        fun useItem(nextTask: Runnable) {
                            isCancelled = true
                            val coolDown = itemManager.getItemCoolDown(uniqueId, item)
                            if (coolDown == null) {
                                swingMainHand()
                                itemManager.setItemCoolDown(uniqueId, item, CustomItem.getDefaultCoolDown(item))
                                item.amount--
                                nextTask.run()
                                sendActionBarWithSound(text("아이템을 사용하였습니다!", NamedTextColor.GREEN))
                            }
                            else sendActionBarWithSound(
                                text(
                                    "${coolDown / 60}분 ${coolDown % 60}초 후 ".replace("0분 ", ""),
                                    NamedTextColor.WHITE
                                ).append(text("사용할 수 있습니다!", NamedTextColor.RED))
                            )
                        }
                        fun useTeleportItem(teleportItem: ItemStack) {
                            useItem {
                                when (teleportItem) {
                                    CustomItem.TELEPORT.SPAWN -> teleport(server.worlds[0]!!.spawnLocation.getRandomLocation(5.0))
                                    CustomItem.TELEPORT.CORE -> {
                                        val teamData = TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID)
                                        teleport(teamData.coreLocation!!.getRandomLocation(5.0))
                                    }
                                }
                            }
                        }
                        fun useSkillItem(ability: TeamAbility, task: Runnable?) {
                            if (TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID).ability == ability) {
                                useItem {
                                    if (task == null) teamAbilityManager.enableSkill(uniqueId)
                                    else task.run()
                                }
                            }
                            else sendActionBarWithSound(text(
                                "해당 아이템을 사용하기 위해서는 ${ability.asTeamAbilityString} 능력이 필요합니다!",
                                NamedTextColor.RED
                            ))
                        }
                        when (val clonedItem = item.clone().apply { amount = 1 }) {
                            CustomItem.TELEPORT.SPAWN, CustomItem.TELEPORT.CORE -> return useTeleportItem(clonedItem)
                            CustomItem.SKILL.CHEETAH -> return useSkillItem(TeamAbility.CHEETAH) {
                                velocity = location.direction.multiply(10).setY(0)
                            }
                            CustomItem.SKILL.TURTLE -> return useSkillItem(TeamAbility.TURTLE, null)
                            CustomItem.SKILL.WILD_BOAR -> return useSkillItem(TeamAbility.WILD_BOAR, null)
                            CustomItem.SKILL.ROE_DEER -> return useSkillItem(TeamAbility.ROE_DEER) {
                                velocity = location.direction.setY(4)
                                server.scheduler.runTaskLater(plugin, Runnable {
                                    velocity = location.direction.setY(4)
                                    server.scheduler.runTaskLater(plugin, Runnable {
                                        velocity = location.direction.setY(4)
                                        server.scheduler.runTaskLater(plugin, Runnable {
                                            velocity = location.direction.setY(4)
                                        }, 20L)
                                    }, 20L)
                                }, 20L)
                            }
                            CustomItem.SKILL.FOUR_LEAF_CLOVER -> return useSkillItem(TeamAbility.FOUR_LEAF_CLOVER, null)
                        }
                    }
                }
                if (clickedBlock != null) {
                    val block = clickedBlock!!
                    val type = block.type
                    if (type == Material.BEACON) {
                        playBellSound()
                        if (TeamDataManager.getData(PlayerDataManager.getData(uniqueId).teamUUID).coreLocation == block.location) {
                            openInventory(coreMenuInventory)
                        }
                        else sendActionBar(text("다른 팀의 팀 코어 메뉴는 열 수 없습니다!", NamedTextColor.RED))
                        isCancelled = true
                    }
                    else if (type == Material.END_PORTAL_FRAME) isCancelled = true
                }
            }
        }
    }
}