package com.snow.zulrah.leafs

import com.snow.zulrah.models.WintertodtLocation
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.Players
import org.powbot.api.script.tree.Leaf

class WalkToSafespot(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Walking to safespot") {
    private val currentLocation: WintertodtLocation get() = WintertodtLocation.SOUTH_WEST

    override fun execute() {
        Movement.builder(currentLocation.safespotTile)
            .setWalkUntil { Players.local().tile() == currentLocation.safespotTile }
            .move()
    }
}