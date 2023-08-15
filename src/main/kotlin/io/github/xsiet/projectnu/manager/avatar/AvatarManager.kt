package io.github.xsiet.projectnu.manager.avatar

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Pair
import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.avatar.AvatarData
import io.github.xsiet.projectnu.data.avatar.AvatarDataManager
import io.github.xsiet.projectnu.data.player.PlayerDataManager
import io.github.xsiet.projectnu.data.team.TeamDataManager
import io.github.xsiet.projectnu.item.CustomItem
import io.github.xsiet.projectnu.utils.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import org.bukkit.inventory.EquipmentSlot
import net.minecraft.world.entity.Pose
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Slime
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class AvatarManager(
    private val plugin: ProjectNUPlugin
) {
    private val bukkitServer = plugin.server
    private val channelManager get() = plugin.channelManager
    private val interactionEntityMap = LinkedHashMap<UUID, Slime>()
    private val interactionEntityUUIDMap = LinkedHashMap<UUID, UUID>()
    private val serverPlayerMap = LinkedHashMap<UUID, ServerPlayer>()
    private val entityUUIDMap = LinkedHashMap<UUID, UUID>()
    val entityIdMap = LinkedHashMap<UUID, Int>()
    private val entityDataMap = LinkedHashMap<UUID, SynchedEntityData>()
    private val packetMap = LinkedHashMap<UUID, ArrayList<Packet<ClientGamePacketListener>>>()
    val inventoryMap = LinkedHashMap<UUID, Inventory>()
    private val uuids = ArrayList<UUID>()
    private val slimeSize = 1
    private val hotBarStartSlot = 45
    private val hotBarEndSlot = 53
    private val inventoryStartSlot = 18
    private val inventoryEndSlot = 44
    fun checkInteractionEntity(entity: Entity) = interactionEntityMap.containsValue(entity)
    fun getAvatarUUID(entity: Entity): UUID {
        var uuid: UUID? = null
        uuids.forEach { if (interactionEntityMap[it] == entity) uuid = it }
        return uuid!!
    }
    init {
        bukkitServer.worlds.forEach {
            it.apply {
                setGameRule(GameRule.KEEP_INVENTORY, true)
                setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
            }
        }
        AvatarDataManager.uuids.forEach { createAvatar(AvatarDataManager.getData(it)) }
        bukkitServer.scheduler.runTaskTimer(plugin, Runnable {
            bukkitServer.worlds.forEach { world ->
                world.entities.forEach {
                    if (it.type == EntityType.SLIME) (it as Slime).apply {
                        if (!checkInteractionEntity(it) && isInvisible && !hasAI() && size == slimeSize) remove()
                    }
                }
            }
            uuids.forEach {
                val data = AvatarDataManager.getData(it)
                if (data.isDead && data.timeout < System.currentTimeMillis()) {
                    val playerData = PlayerDataManager.getData(it)
                    TeamDataManager.getData(playerData.teamUUID).apply {
                        if (coreLocation == null) data.location = spawnLocation.getRandomLocation(5.0)
                        else data.location = coreLocation!!.getRandomLocation(5.0)
                    }
                    data.apply {
                        isDead = false
                        location.apply {
                            interactionEntityMap[it]!!.teleport(this)
                            serverPlayerMap[it]!!.setPos(x, y, z)
                        }
                    }
                    ClientboundTeleportEntityPacket(serverPlayerMap[it]!!).apply {
                        bukkitServer.sendPacket(this)
                        packetMap[it]!!.add(this)
                    }
                    channelManager.sendPlayerLogMessageToTeamChannel(
                        playerData,
                        Color.CYAN,
                        NamedTextColor.AQUA,
                        "${playerData.nickname}님의 아바타가 부활하였습니다!",
                        true
                    )
                }
                if (!data.location.getNearbyEntities(10.0, 10.0, 10.0).isEmpty()) {
                    if (bukkitServer.getEntity(interactionEntityUUIDMap[it]!!) == null) spawnInteractionEntity(it, data.location)
                    inventoryMap[it]?.apply {
                        val equipmentItemStacks = arrayListOf(
                            getItemStack(2),
                            getItemStack(3),
                            getItemStack(4),
                            getItemStack(5),
                            getItemStack(6)
                        )
                        interactionEntityMap[it]?.equipment?.apply {
                            helmet = equipmentItemStacks[0]
                            chestplate = equipmentItemStacks[1]
                            leggings = equipmentItemStacks[2]
                            boots = equipmentItemStacks[3]
                            setItemInOffHand(equipmentItemStacks[4])
                        }
                        val entityId = entityIdMap[it]
                        fun getNMSItemStack(index: Int) = CraftItemStack.asNMSCopy(equipmentItemStacks[index])
                        if (entityId != null) {
                            bukkitServer.sendPacket(arrayListOf(
                                ClientboundSetEquipmentPacket(entityId, arrayListOf(
                                    Pair(net.minecraft.world.entity.EquipmentSlot.HEAD, getNMSItemStack(0)),
                                    Pair(net.minecraft.world.entity.EquipmentSlot.CHEST, getNMSItemStack(1)),
                                    Pair(net.minecraft.world.entity.EquipmentSlot.LEGS, getNMSItemStack(2)),
                                    Pair(net.minecraft.world.entity.EquipmentSlot.FEET, getNMSItemStack(3)),
                                    Pair(net.minecraft.world.entity.EquipmentSlot.OFFHAND, getNMSItemStack(4))
                                ))
                            ))
                        }
                        data.apply {
                            equipments = equipmentItemStacks
                            hotBarItems = ArrayList<ItemStack>().apply {
                                for (slot: Int in hotBarStartSlot..hotBarEndSlot) add(getItemStack(slot))
                            }
                            inventoryItems = ArrayList<ItemStack>().apply {
                                for (slot: Int in inventoryStartSlot..inventoryEndSlot) add(getItemStack(slot))
                            }
                        }
                    }
                }
            }
        }, 0, 1)
    }
    private fun spawnInteractionEntity(uuid: UUID, location: Location) {
        interactionEntityMap[uuid] = location.world.spawn(location, Slime::class.java) {
            it.apply {
                isInvisible = true
                setAI(false)
                size = slimeSize
                interactionEntityUUIDMap[uuid] = uniqueId
            }
        }
    }
    fun sendPackets(player: Player) {
        packetMap.values.forEach { player.sendPacket(it) }
        bukkitServer.scheduler.runTaskLater(plugin, Runnable {
            player.sendPacket(ClientboundPlayerInfoRemovePacket(entityUUIDMap.values.toList()))
        }, 200L)
    }
    fun createAvatar(data: AvatarData) {
        bukkitServer.scheduler.runTask(plugin, Runnable {
            val ownerUUID = data.uuid
            val location = data.location
            spawnInteractionEntity(ownerUUID, location)
            serverPlayerMap[ownerUUID] = ServerPlayer(
                (bukkitServer as CraftServer).handle.server,
                (location.world as CraftWorld).handle.level,
                GameProfile(UUID.randomUUID(), data.name)
            ).apply {
                location.apply { setPos(x, y, z) }
                gameProfile.setSkin(data.skinUUID)
                entityData.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127)
                pose = Pose.SLEEPING
                ownerUUID.apply {
                    entityUUIDMap[this] = uuid
                    entityIdMap[this] = bukkitEntity.entityId
                    entityDataMap[this] = entityData
                }
                packetMap[ownerUUID] = arrayListOf(
                    ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this),
                    ClientboundAddPlayerPacket(this),
                    ClientboundSetEntityDataPacket(id, entityData.packDirty()!!),
                    ClientboundRotateHeadPacket(this, 0.toByte())
                ).apply { bukkitServer.sendPacket(this) }
                bukkitServer.scheduler.runTaskLater(plugin, Runnable {
                    bukkitServer.sendPacket(ClientboundPlayerInfoRemovePacket(entityUUIDMap.values.toList()))
                }, 200L)
                bukkitServer.createInventory(null, 54, text("누군가의 보관함")).apply {
                    arrayListOf(0, 1, 7, 8).forEach { setItem(it, CustomItem.LOCKED.SLOT) }
                    for (slot: Int in 9..17) setItem(slot, CustomItem.LOCKED.SLOT)
                    data.apply {
                        for (slot: Int in 2..6) setItem(slot, equipments[slot - 2])
                        for (slot: Int in hotBarStartSlot..hotBarEndSlot) {
                            setItem(slot, hotBarItems[slot - hotBarStartSlot])
                        }
                        for (slot: Int in inventoryStartSlot..inventoryEndSlot) {
                            setItem(slot, inventoryItems[slot - inventoryStartSlot])
                        }
                    }
                    inventoryMap[ownerUUID] = this
                }
            }
            uuids.add(ownerUUID)
        })
    }
    private fun removeAvatar(uuid: UUID) {
        AvatarDataManager.getData(uuid).delete()
        uuids.remove(uuid)
        inventoryMap[uuid]!!.apply {
            close()
            clear()
        }
        packetMap.remove(uuid)
        entityDataMap.remove(uuid)
        entityIdMap[uuid]?.apply {
            entityIdMap.remove(uuid)
            bukkitServer.sendPacket(ClientboundRemoveEntitiesPacket(this))
        }
        entityUUIDMap.remove(uuid)
        serverPlayerMap.remove(uuid)
        interactionEntityMap[uuid]?.apply { remove() }
        interactionEntityMap.remove(uuid)
    }
    fun connectAvatar(player: Player) {
        val uuid = player.uniqueId
        AvatarDataManager.getData(uuid).apply {
            player.isInvulnerable = true
            player.nickname = name
            player.skinUUID = skinUUID
            player.health = health
            player.genericMaxHealth = maxHealth
            player.foodLevel = foodLevel
            player.inventory.apply {
                equipments.apply {
                    setItem(EquipmentSlot.HEAD, get(0))
                    setItem(EquipmentSlot.CHEST, get(1))
                    setItem(EquipmentSlot.LEGS, get(2))
                    setItem(EquipmentSlot.FEET, get(3))
                    setItem(EquipmentSlot.OFF_HAND, get(4))
                }
                hotBarItems.apply { for (slot: Int in 0..8) setItem(slot, get(slot)) }
                inventoryItems.apply { for (slot: Int in 9..35) setItem(slot, get(slot - 9)) }
            }
            bukkitServer.scheduler.apply{
                runTaskLater(plugin, Runnable { player.teleport(location) }, 1)
                runTaskLater(plugin, Runnable { player.isInvulnerable = false }, 40L)
            }
        }
        removeAvatar(uuid)
        PlayerDataManager.getData(uuid).apply {
            channelManager.sendPlayerLogMessageToTeamChannel(
                this,
                Color.GREEN,
                NamedTextColor.GREEN,
                "${nickname}님이 아바타와 연결되셨습니다!",
                false
            )
        }
    }
    fun disconnectAvatar(player: Player) {
        val uuid = player.uniqueId
        AvatarDataManager.getData(uuid).apply {
            if (!isDead) {
                location = player.location
                name = player.nickname
                skinUUID = player.skinUUID
                health = player.health
                maxHealth = player.genericMaxHealth
                foodLevel = player.foodLevel
                isDead = false
                player.inventory.apply {
                    equipments = arrayListOf(
                        getItem(EquipmentSlot.HEAD),
                        getItem(EquipmentSlot.CHEST),
                        getItem(EquipmentSlot.LEGS),
                        getItem(EquipmentSlot.FEET),
                        getItem(EquipmentSlot.OFF_HAND)
                    ).apply { if (get(1) == CustomItem.NO_RECYCLABLE_ELYTRA) set(1, ItemStack(Material.AIR)) }
                    hotBarItems = ArrayList<ItemStack>().apply { for (slot: Int in 0..8) add(getItemStack(slot)) }
                    inventoryItems = ArrayList<ItemStack>().apply { for (slot: Int in 9..35) add(getItemStack(slot)) }
                }
                createAvatar(this)
            }
        }
        PlayerDataManager.getData(uuid).apply {
            channelManager.sendPlayerLogMessageToTeamChannel(
                this,
                Color.GREEN,
                NamedTextColor.GREEN,
                "${nickname}님이 아바타와 연결을 끊으셨습니다!",
                false
            )
        }
    }
    fun killAvatar(uuid: UUID, killerUUID: UUID?) {
        val timeoutHours = 3
        if (!uuids.contains(uuid)) bukkitServer.getPlayer(uuid)!!.kick(
            text("아바타 사망으로 인해 아바타와 연결이 끊겼습니다!\n\n", NamedTextColor.RED)
                .append(text("${timeoutHours}시간 후 ", NamedTextColor.WHITE))
                .append(text("아바타가 자동 부활됩니다!\n(또는 팀원에게 즉시 부활을 요청해 보세요!)", NamedTextColor.GRAY))
        )
        AvatarDataManager.getData(uuid).apply {
            timeout = System.currentTimeMillis().plus(timeoutHours * 3.6e+6).toLong()
            isDead = true
            health = maxHealth
            foodLevel = 20
            location.world.apply {
                playSound(location, "entity.firework_rocket.blast")
                spawnParticle(Particle.FLASH, location, 1)
            }
        }
        PlayerDataManager.getData(uuid).apply {
            var message = "${nickname}님의 아바타가 사망하였습니다!"
            if (killerUUID != null) {
                message = "${nickname}님의 아바타가 ${PlayerDataManager.getData(killerUUID).nickname}님에 의해 사망하였습니다!"
            }
            channelManager.sendPlayerLogMessageToTeamChannel(
                this,
                Color.RED,
                NamedTextColor.RED,
                "$message (${timeoutHours}시간 후 아바타가 자동 부활됩니다!)",
                true
            )
        }
    }
}