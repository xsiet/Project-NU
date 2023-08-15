package io.github.xsiet.projectnu.utils

import org.bukkit.Location
import org.bukkit.World
import kotlin.random.Random

fun World.playSound(location: Location, key: String, volume: Float) {
    playSound(location, key, volume, 1F)
}
fun World.playSound(location: Location, key: String) {
    playSound(location, key, 1F)
}
fun World.getRandomLocation(): Location {
    val range = (worldBorder.size / 2) - 20
    val x = Random.nextDouble(-range, range)
    val z = Random.nextDouble(-range, range)
    return Location(this, x, getHighestBlockYAt(x.toInt(), z.toInt()) + 1.0, z)
}