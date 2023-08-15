package io.github.xsiet.projectnu.utils

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import org.bukkit.Server

fun Server.sendPacket(packet: Packet<ClientGamePacketListener>) = onlinePlayers.forEach { it.sendPacket(packet) }
fun Server.sendPacket(packets: ArrayList<Packet<ClientGamePacketListener>>) = onlinePlayers.forEach { it.sendPacket(packets) }
