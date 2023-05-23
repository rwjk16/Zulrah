package org.zulrah.branch

import com.snow.zulrah.Script
import com.snow.zulrah.leafs.*
import org.Constants.Companion.RING_OF_RECOIL
import org.powbot.api.rt4.Bank
import org.powbot.api.rt4.Inventory
import org.powbot.api.script.tree.Branch
import org.powbot.api.script.tree.TreeComponent
import org.zulrah.leafs.*
import java.util.logging.Logger

class IsGeared(script: Script) : Branch<Script>(script, "Has required items") {
    override val successComponent: TreeComponent<Script> = CanEnterArena(script) // go to kill snek
    override val failedComponent: TreeComponent<Script> = IsBankOpened(script) // must bank

    override fun validate(): Boolean {
        return script.status.isGeared
    }
}

class CanEnterArena(script: Script) : Branch<Script>(script, "Trying to walk to arena") {
    override val successComponent: TreeComponent<Script> = CanCrossStone(script) // go to kill snek
    override val failedComponent: TreeComponent<Script> = TeleToArena(script) // must bank

    override fun validate(): Boolean {
        return false
    }
}

class CanCrossStone(script: Script) : Branch<Script>(script, "Checking if we need to cross") {
    override val successComponent: TreeComponent<Script> = CrossStone(script) // go to kill snek
    override val failedComponent: TreeComponent<Script> = EnterArena(script) // must bank

    override fun validate(): Boolean {
        return false
    }
}

//class NeedsToReset(script: Script) : Branch<Script>(script, "Are we reseting?") {
//    override val successComponent: TreeComponent<Script> = Banking(script) // repair gear or recharge
//    override val failedComponent: TreeComponent<Script> = HasGearToUpgrade(script)
//
//    override fun validate(): Boolean {
//        return Equipment.get().contains()
//    }
//}

class NeedsToRepair(script: Script) : Branch<Script>(script, "Do we need to repair?") {
    override val successComponent: TreeComponent<Script> = CheckRepairs(script) // repair gear or recharge
    override val failedComponent: TreeComponent<Script> = HasGearToUpgrade(script)

    override fun validate(): Boolean {
        return script.status.needsRepair // check if gear is degraded or low on charge
    }
}

class CheckRepairs(script: Script) : Branch<Script>(script, "Which type of repair?") {
    override val successComponent: TreeComponent<Script> = Recharge(script)
    override val failedComponent: TreeComponent<Script> = Banking(script)

    override fun validate(): Boolean {
        return Inventory.stream().name(RING_OF_RECOIL).isNotEmpty() // check if degraged staff or BP, we can wait for message to trigger some flag
    }
}

class IsBankOpened(script: Script) : Branch<Script>(script, "Is bank opened?") {
    override val successComponent: TreeComponent<Script> = Banking(script)
    override val failedComponent: TreeComponent<Script> = NeedsToRepair(script)

    override fun validate(): Boolean {
        return Bank.opened()
    }
}

class HasGearToEquipForBank(script: Script) : Branch<Script>(script, "Has gear to equip to bank?") {
    override val successComponent: TreeComponent<Script> = EquipMageGear(script) // equip gear
    override val failedComponent: TreeComponent<Script> = OpenBank(script)

    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun validate(): Boolean {

        val setup = script.configuration.mageSetup
        val inventory = Inventory.get()

        logger.info("PRIMARY SETUP: ${setup.map { it.name() }}")

        script.configuration.mageSetup.forEach { i ->
            logger.info("SEARCHING FOR ${i.name()}")
            if (Inventory.stream().name(i.name()).isNotEmpty() && i.name() != RING_OF_RECOIL) {
                logger.info("INVENTORY: ${Inventory.get().map { it.name() }}\n" +
                        "FOUND ${i.name()}")
                return true
            } else {
                return@forEach
            }
        }
        logger.info("NONE FOUND")
        return false
    }
}
class HasGearToUpgrade(script: Script) : Branch<Script>(script, "Has gear to upgrade?") {
    override val successComponent: TreeComponent<Script> = EquipGearIfUpgraded(script)
    override val failedComponent: TreeComponent<Script> = HasGearToEquipForBank(script)

    override fun validate(): Boolean {
        // TODO: Add gear upgrade option
//        if (!script.configuration.upgradeGear) {
//            return false
//        }

//        val equipment = Equipment.get()
//        val pyroInventory = Inventory.stream().name(*ITEMS_PYROMANCER).toList()
//
//        pyroInventory.forEach {
//            if (!equipment.any { eq -> eq.name() == it.name() }) {
//                return true
//            }
//        }
        return false
    }
}
