package io.github.xsiet.projectnu.item

import io.github.xsiet.projectnu.manager.team.TeamAbility
import io.github.xsiet.projectnu.manager.team.asTeamAbilityString
import io.github.xsiet.projectnu.manager.team.textColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object CustomItem {
    private val defaultCoolDownMap = LinkedHashMap<ItemStack, Int>()
    fun getDefaultCoolDown(item: ItemStack) = defaultCoolDownMap[item.clone().apply { amount = 1 }]!!
    private fun createCustomItem(type: Material, displayName: Component) = ItemStack(type).apply {
        itemMeta = itemMeta.apply { displayName(displayName.decoration(TextDecoration.ITALIC, false)) }
    }
    private fun createCustomItem(
        type: Material,
        customModelData: Int,
        displayName: Component,
        lore: ArrayList<Component>
    ) = createCustomItem(type, displayName).apply {
        itemMeta = itemMeta.apply {
            setCustomModelData(customModelData)
            lore(ArrayList<Component>().apply {
                lore.forEach { add(it.decoration(TextDecoration.ITALIC, false)) }
            })
        }
    }
    private fun createTeleportItem(customModelData: Int, locationName: String, textColor: NamedTextColor, coolDown: Int) = createCustomItem(
        Material.PAPER,
        customModelData,
        text("순간 이동 아이템 : $locationName", textColor),
        arrayListOf(
            text("사용 시 $locationName 주변으로 순간 이동됩니다!", NamedTextColor.WHITE),
            text(""),
            text("재사용 대기시간", NamedTextColor.RED)
                .append(text(": ", NamedTextColor.WHITE))
                .append(text("${coolDown / 60}분 ${coolDown % 60}초", NamedTextColor.GOLD))
        )
    ).apply { defaultCoolDownMap[this] = coolDown }
    private fun createSkillItem(customModelData: Int, teamAbility: TeamAbility, description: String, coolDown: Int) = createCustomItem(
        Material.PAPER,
        customModelData,
        text("스킬 아이템 : ${teamAbility.asTeamAbilityString}", teamAbility.textColor),
        arrayListOf(
            text("사용 시 스킬이 발동됩니다!", NamedTextColor.WHITE),
            text(""),
            text("스킬", NamedTextColor.AQUA)
                .append(text(": ", NamedTextColor.WHITE))
                .append(text(description, NamedTextColor.YELLOW)),
            text("재사용 대기시간", NamedTextColor.RED)
                .append(text(": ", NamedTextColor.WHITE))
                .append(text("${coolDown / 60}분 ${coolDown % 60}초", NamedTextColor.GOLD))
        )
    ).apply { defaultCoolDownMap[this] = coolDown }
    val NO_RECYCLABLE_ELYTRA = createCustomItem(
        Material.ELYTRA,
        0,
        text("1회용 겉날개", NamedTextColor.GOLD),
        arrayListOf(
            text("한번 착용 시 착용을 해제할 수 없으며,", NamedTextColor.WHITE),
            text("서버를 나가거나 비행을 종료하면 사라집니다!", NamedTextColor.WHITE),
            text("(비행 시작 시 피해 없는 넉백을 받습니다!)", NamedTextColor.WHITE)
        )
    ).apply {
        addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        itemMeta = itemMeta.apply { isUnbreakable = true }
    }
    object LOCKED {
        val SLOT = createCustomItem(Material.GRAY_STAINED_GLASS_PANE, text(""))
    }
    object MENU {
        val AVATAR_REVIVAL = createCustomItem(
            Material.PAPER,
            1,
            text("아바타 부활", NamedTextColor.LIGHT_PURPLE),
            arrayListOf(text("클릭 시 아바타 부활 메뉴를 엽니다!", NamedTextColor.WHITE))
        )
        val NUNIUM_SHOP = createCustomItem(
            Material.PAPER,
            2,
            text("누늄 상점", NamedTextColor.YELLOW),
            arrayListOf(text("클릭 시 누늄 상점 메뉴를 엽니다!", NamedTextColor.WHITE))
        )
    }
    object TELEPORT {
        val SPAWN = createTeleportItem(3, "스폰", NamedTextColor.GREEN, 1 * 60)
        val CORE = createTeleportItem(4, "팀 코어", NamedTextColor.BLUE, 1 * 60)
    }
    object SKILL {
        val CHEETAH = createSkillItem(
            5,
            TeamAbility.CHEETAH,
            "즉시 10블록 대쉬",
            1 * 60
        )
        val TURTLE = createSkillItem(
            6,
            TeamAbility.TURTLE,
            "1분 동안 피해 30% 감소",
            8 * 60
        )
        val WILD_BOAR = createSkillItem(
            7,
            TeamAbility.WILD_BOAR,
            "1분 동안 패시브 효과 8배 증폭",
            12 * 60
        )
        val ROE_DEER = createSkillItem(
            8,
            TeamAbility.ROE_DEER,
            "즉시 200블럭 점프",
            12 * 60
        )
        val FOUR_LEAF_CLOVER = createSkillItem(
            9,
            TeamAbility.FOUR_LEAF_CLOVER,
            "1분 동안 공격 시 40% 확률로 피해 두 배",
            1 * 60 + 30
        )
    }
}