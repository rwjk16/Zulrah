package org.zulrah.branch

import com.snow.zulrah.Script
import com.snow.zulrah.leafs.ExitArena
import com.snow.zulrah.leafs.LightInitialFire
import org.Constants.Companion.ZUL_INST_AREA
import org.powbot.api.rt4.Players
import org.powbot.api.script.tree.Branch
import org.powbot.api.script.tree.TreeComponent

class IsInside(script: Script) : Branch<Script>(script, "Is inside arena") {
    override val successComponent: TreeComponent<Script> = IsGameRunning(script)
    override val failedComponent: TreeComponent<Script> = IsGeared(script)

    override fun validate(): Boolean {
        return ZUL_INST_AREA.contains(Players.local().tile())
    }
}

class HasMinimumFood(script: Script) : Branch<Script>(script, "Has minimum food") {
    override val successComponent: TreeComponent<Script> = LightInitialFire(script)
    override val failedComponent: TreeComponent<Script> = ExitArena(script)

    override fun validate(): Boolean {
        return false
    }
}