package org.zulrah.branch

import com.snow.zulrah.OpenWintertodtConstants.AREA_NEAR_DOOR
import com.snow.zulrah.OpenWintertodtConstants.ITEMS_USELESS
import com.snow.zulrah.OpenWintertodtConstants.ITEM_BRUMA_KINDLING
import com.snow.zulrah.Script
import com.snow.zulrah.extensions.count
import com.snow.zulrah.helpers.FoodHelper
import com.snow.zulrah.leafs.*
import org.powbot.api.Condition
import org.powbot.api.rt4.Inventory
import org.powbot.api.rt4.Players
import org.powbot.api.rt4.Prayer
import org.powbot.api.script.tree.Branch
import org.powbot.api.script.tree.SimpleLeaf
import org.powbot.api.script.tree.TreeComponent
import java.util.logging.Logger

class IsGameRunning(script: Script) : Branch<Script>(script, "Is game running") {
    override val successComponent: TreeComponent<Script> = ShouldMove(script) // check vs other phase
    override val failedComponent: TreeComponent<Script> = HasMinimumFood(script) // loot, then exit

    override fun validate(): Boolean {
        return false// Zulrah is present
    }
}

class ShouldMove(script: Script) : Branch<Script>(script, "Should move") {
    override val successComponent: TreeComponent<Script> = Move(script) // Move
    override val failedComponent: TreeComponent<Script> = NeedsToEat(script) // check if we need to eat

    override fun validate(): Boolean {
        return false //validateCurrentZulrahRound() != script.status.currentZulRound - here we should check the current prayer for the round, then switch
    }

    // private fun validateCurrentZulrahRound(): Boolean {
    // }
}

class NeedsToEat(script: Script) : Branch<Script>(script, "Needs to eat") {
    override val successComponent: TreeComponent<Script> = HasFoodToEat(script)
    override val failedComponent: TreeComponent<Script> = ShouldChangePrayer(script)

    override fun validate(): Boolean {
        return FoodHelper.needToEat(script)
    }
}

class ShouldChangePrayer(script: Script) : Branch<Script>(script, "Switch prayer?") {
    override val successComponent: TreeComponent<Script> = SimpleLeaf(script, "Dropping useless items") {
        val currentPhase = script.status.currentZulrahPhase

        val prayer = currentPhase.pray
        val boost = currentPhase.getPrayerBoost()

        prayer?.let { Prayer.prayer(it, true) }
        boost?.let { Prayer.prayer(it, true) }
    }
    override val failedComponent: TreeComponent<Script> = ShouldStayInSafeZone(script) // attack

    override fun validate(): Boolean {
        script.status.currentZulrahPhase.getPrayerBoost()?.let {
            return !Prayer.prayerActive(it)
        } ?: run {
            return false
        }

    }
}

class ShouldStayInSafeZone(script: Script) : Branch<Script>(script, "Is in safezone") {
    override val successComponent: TreeComponent<Script> = Move(script)
    override val failedComponent: TreeComponent<Script> = ShouldStartLightingInventory(script)

    override fun validate(): Boolean {
        return AREA_NEAR_DOOR.contains(Players.local()) && Inventory.stream()
            .name(*FoodHelper.getFoodInformation(script.configuration.foodName))
            .count() <= 0
    }
}

class ShouldStartLightingInventory(script: com.snow.zulrah.Script) : Branch<com.snow.zulrah.Script>(script, "Lighting stuff") {

    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override val successComponent: TreeComponent<Script> = CanLightFire(script)
    override val failedComponent: TreeComponent<Script> = ShouldWalkToSafespot(script)

    // TODO Figure out how to split it better between different configurations without duplicate counts
    override fun validate(): Boolean {
//        // If its fully crafted and fletching enabled
//        val kindlingCount = Inventory.count(ITEM_BRUMA_KINDLING)
//        val rootCount = Inventory.count(ITEM_BRUMA_ROOT)
//        val inventoryFull = Inventory.isFull()
//        var result = false
//        if (script.status.lighting && kindlingCount + rootCount > 0) {
//            result = true
//            logger.info("Lighting because has remaining")
//        } else if (inventoryFull && script.configuration.logsOnly) {
//            result = true
//            logger.info("Lighting because only logs")
//        } else if (inventoryFull && rootCount == 0) {
//            result = true
//            logger.info("Lighting full kindling")
//        } else if (rootCount == 0 && kindlingCount > 0) {
//            result = true
//            logger.info("Lighting since no logs to fletch")
//        } else if (rootCount + kindlingCount >= remainingHealthPercentage()) {
//            logger.info("Lighting because $rootCount, $kindlingCount, ${remainingHealthPercentage()}%")
//            result = true
//        }

        return false //result.also { script.status.lighting = it }
    }
}

class CanLightFire(script: com.snow.zulrah.Script) : Branch<com.snow.zulrah.Script>(script, "Can light fire") {

    override val successComponent: TreeComponent<com.snow.zulrah.Script> = LightingBrazier(script)
    override val failedComponent: TreeComponent<com.snow.zulrah.Script> = UpdateLocation(script)

    override fun validate(): Boolean {
        return false //isBrazierAlive(script.status.currentLocation) || !isPyromancerDead(script.status.currentLocation)
    }

}

class ShouldWalkToSafespot(script: com.snow.zulrah.Script) : Branch<com.snow.zulrah.Script>(script, "Should walk to safespot") {
    override val successComponent: TreeComponent<com.snow.zulrah.Script> = WalkToSafespot(script)
    override val failedComponent: TreeComponent<com.snow.zulrah.Script> = ShouldChopVines(script)

    override fun validate(): Boolean {
        return false //script.configuration.snowfallSafespot &&
                //Players.local().tile() != script.status.currentLocation.safespotTile
    }
}

class ShouldChopVines(script: Script) : Branch<Script>(script, "Should chop vines") {
    override val successComponent: TreeComponent<com.snow.zulrah.Script> = ChoppingVines(script)
    override val failedComponent: TreeComponent<com.snow.zulrah.Script> = FletchLogs(script)

    override fun validate(): Boolean {
        return !Inventory.isFull() && Inventory.count(ITEM_BRUMA_KINDLING) == 0
    }
}

class HasFoodToEat(script: Script) : Branch<Script>(script, "Has food to eat") {
    override val successComponent: TreeComponent<Script> = EatFood(script)
    override val failedComponent: TreeComponent<Script> = Move(script) // tele out

    override fun validate(): Boolean {
        return Inventory.count(*FoodHelper.getFoodInformation(script.configuration.foodName)) > 0
    }
}