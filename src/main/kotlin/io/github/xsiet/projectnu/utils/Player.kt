package io.github.xsiet.projectnu.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

fun Player.playSound(key: String, volume: Float, pitch: Float) {
    playSound(Sound.sound(Key.key(key), Sound.Source.PLAYER, volume, pitch))
}
fun Player.playSound(key: String, volume: Float) {
    playSound(key, volume, 1F)
}
fun Player.playSound(key: String) {
    playSound(key, 1F)
}
fun Player.playBellSound() = playSound("block.note_block.bell")
fun Player.sendActionBarWithSound(message: Component) {
    playBellSound()
    sendActionBar(message)
}
fun Player.addPotionEffect(type: PotionEffectType, duration: Int, amplifier: Int) {
    addPotionEffect(PotionEffect(type, duration * 20, amplifier - 1, false, false, false))
}
fun Player.sendPacket(packet: Packet<ClientGamePacketListener>) = (this as CraftPlayer).handle.connection.send(packet)
fun Player.sendPacket(packets: ArrayList<Packet<ClientGamePacketListener>>) = packets.forEach { sendPacket(it) }
fun Player.sendInfoUpdatePackets() = sendPacket(arrayListOf(
    ClientboundPlayerInfoRemovePacket(arrayListOf(uniqueId)),
    ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, (this as CraftPlayer).handle)
))
private val playerNicknames = LinkedHashMap<Player, String>()
var Player.nickname
    get() = playerNicknames[this] ?: name
    set(value) {
        playerNicknames[this] = value
        (this as CraftPlayer).handle.apply {
            gameProfile.javaClass.getDeclaredField("name").apply {
                isAccessible = true
                set(gameProfile, value)
            }
        }
        sendInfoUpdatePackets()
    }
private val playerSkinUUIDs = LinkedHashMap<Player, UUID>()
var Player.skinUUID
    get() = playerSkinUUIDs[this] ?: this.uniqueId
    set(value) {
        playerSkinUUIDs[this] = value
        if (uniqueId != value) {
            (this as CraftPlayer).handle.apply { gameProfile.setSkin(value) }
            sendInfoUpdatePackets()
        }
    }
var Player.genericMaxHealth
    get() = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
    set(value) { getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = value }