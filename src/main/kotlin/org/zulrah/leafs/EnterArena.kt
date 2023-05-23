package org.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants
import com.snow.zulrah.Script
import org.powbot.api.Condition
import org.powbot.api.Tile
import org.powbot.api.rt4.GameObject
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.Objects
import org.powbot.api.rt4.Players
import org.powbot.api.rt4.walking.local.LocalPathFinder
import org.powbot.api.script.tree.Leaf

class EnterArena(script: Script) : Leaf<Script>(script, "Teleing To arena") {

    override fun execute() {
        enterArena()
    }

    private fun enterArena() {

        val door = Objects.stream(20, GameObject.Type.INTERACTIVE).name(OpenWintertodtConstants.OBJECT_DOOR)
            .first()

        if (door == GameObject.Nil) {
            Movement.builder(OpenWintertodtConstants.TILE_NEAR_DOOR_OUTSIDE)
                .setWalkUntil { OpenWintertodtConstants.TILE_NEAR_DOOR_OUTSIDE.distance() < 2 }
                .setRunMin(50)
                .setRunMax(70)
                .move()
        } else {
            walkToDoor(door)
        }
    }

    private fun walkToDoor(boat: GameObject) {
        if (!boat.inViewport() || boat.tile.distance() >= 3) {
            val targetTile = Tile(boat.tile.x, 3520)
            LocalPathFinder.findPath(targetTile)
                .traverseUntilReached(3.0)
        }

        if (boat.inViewport() && boat.interact(OpenWintertodtConstants.ACTION_ENTER)) {
            Condition.wait({ OpenWintertodtConstants.AREA_INSIDE_ARENA.contains(Players.local()) }, 1000, 10)
        }
    }
}