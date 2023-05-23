package com.snow.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.AREA_NEAR_DOOR
import org.powbot.api.Condition
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.Players
import org.powbot.api.rt4.Varpbits
import org.powbot.api.script.tree.Leaf

class Move(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Idling in safe zone") {
    override fun execute() {

        if (AREA_NEAR_DOOR.contains(Players.local())) {
            Condition.wait { Varpbits.varpbit(com.snow.zulrah.OpenWintertodtConstants.VARPBIT_RESPAWN) != 0 }
        } else {
            Movement.builder(AREA_NEAR_DOOR.randomTile)
                .setWalkUntil { AREA_NEAR_DOOR.contains(Players.local()) ||
                        Varpbits.varpbit(com.snow.zulrah.OpenWintertodtConstants.VARPBIT_RESPAWN) != 0 }
                .move()
        }
    }
}