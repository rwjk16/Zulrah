package com.snow.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.ACTION_LIGHT
import com.snow.zulrah.OpenWintertodtConstants.OBJECT_BRAZIER
import com.snow.zulrah.OpenWintertodtConstants.OBJECT_BURNING_BRAZIER
import com.snow.zulrah.OpenWintertodtConstants.VARPBIT_RESPAWN
import com.snow.zulrah.extensions.Conditions
import com.snow.zulrah.models.WintertodtLocation
import org.powbot.api.Condition
import org.powbot.api.Random
import org.powbot.api.Tile
import org.powbot.api.rt4.*
import org.powbot.api.script.tree.Leaf

class LightInitialFire(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Light initial fire") {
    private val currentLocation: WintertodtLocation get() = WintertodtLocation.SOUTH_WEST

    override fun execute() {
        val brazierTile = currentLocation.brazierTile
        if (Players.local().y() != currentLocation.brazierY || brazierTile.distanceTo(Players.local()) >= 2.5) {
            val xPos = Random.nextInt(currentLocation.minBrazierX, currentLocation.maxBrazierX)
            val tile = Tile(xPos, currentLocation.brazierY)
            Movement.builder(tile).setWalkUntil { Players.local().y() == currentLocation.brazierY }.move()
        }

        if (brazierTile.distanceTo(Players.local()) <= 2.5 && shouldLightInitialFire()) {

            val brazier = getBrazier()
            if (brazier.interact(ACTION_LIGHT, Game.singleTapEnabled())) {
                Condition.wait(Conditions.expGained(Constants.SKILLS_FIREMAKING))
            }
        }
    }

    private fun shouldLightInitialFire(): Boolean {
        val ticksValue = Varpbits.varpbit(VARPBIT_RESPAWN)
        return ticksValue <= 32768
    }

    private fun getBrazier(): GameObject {
        return Objects.stream(currentLocation.brazierTile, 4, GameObject.Type.INTERACTIVE)
            .name(OBJECT_BURNING_BRAZIER, OBJECT_BRAZIER)
            .nearest()
            .first()
    }
}