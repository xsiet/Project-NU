package io.github.xsiet.projectnu.utils

import org.bukkit.Location
import kotlin.random.Random

fun Location.getRandomLocation(range: Double): Location {
    var randomX = x + Random.nextDouble(-range, range)
    var randomZ = z + Random.nextDouble(-range, range)
    var newY = world.getHighestBlockYAt(randomX.toInt(), randomZ.toInt())
    if (newY == 319) {
        randomX ++
        randomZ ++
        newY = world.getHighestBlockYAt(x.toInt(), z.toInt())
    }
    return Location(world, randomX, newY + 1.0, randomZ)
}