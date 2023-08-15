package io.github.xsiet.projectnu.manager.team

import io.github.xsiet.projectnu.ProjectNUPlugin
import io.github.xsiet.projectnu.data.team.TeamDataManager
import org.bukkit.Location
import org.bukkit.block.Block

class TeamCoreManager(
    private val plugin: ProjectNUPlugin
) {
    fun checkTeamCoreMaintenanceBlock(block: Block): Boolean {
        var result = false
        TeamDataManager.uuids.forEach { teamUUID ->
            TeamDataManager.getData(teamUUID).apply {
                if (coreLocation != null) {
                    val core = coreLocation!!.block
                    ArrayList<Location>().apply {
                        for (x: Int in (core.x - 1)..(core.x + 1)) {
                            for (z: Int in (core.z - 1)..(core.z + 1)) add(
                                Location(
                                    core.world,
                                    x.toDouble(),
                                    (core.y - 1).toDouble(),
                                    z.toDouble()
                                )
                            )
                        }
                        if (contains(block.location)) result = true
                    }
                    ArrayList<Location>().apply {
                        for (y: Int in (core.y + 1)..319) add(
                            Location(
                                core.world,
                                core.x.toDouble(),
                                y.toDouble(),
                                core.z.toDouble()
                            )
                        )
                        if (contains(block.location)) result = true
                    }
                }
            }
        }
        return result
    }
}