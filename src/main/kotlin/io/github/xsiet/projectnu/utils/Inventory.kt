package io.github.xsiet.projectnu.utils

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.getItemStack(slot: Int) = getItem(slot) ?: ItemStack(Material.AIR)