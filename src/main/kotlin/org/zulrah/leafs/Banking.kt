package org.zulrah.leafs

import com.snow.zulrah.Script
import org.Constants.Companion.ANTI_VENOM
import org.Constants.Companion.KARAMBWAN
import org.Constants.Companion.PRAY_POT
import org.Constants.Companion.RING_OF_RECOIL
import org.powbot.api.Condition
import org.powbot.api.Notifications
import org.powbot.api.rt4.Bank
import org.powbot.api.rt4.Equipment
import org.powbot.api.rt4.Inventory
import org.powbot.api.rt4.Item
import org.powbot.api.script.tree.Leaf
import org.powbot.mobile.script.ScriptManager
import java.util.logging.Logger
import kotlin.math.abs

class Banking(script: Script) : Leaf<Script>(script, "Banking") {
    private var primarySetup: List<Item> = script.configuration.mageSetup
    private var secondaryLoadOut: List<Item> = script.configuration.rangeSetup
    private lateinit var idealLayout: Map<String, Int>
    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun execute() {
//        ensureEnoughFood()
        ensureCorrectGearEquipped()
        idealLayout = buildIdealInventoryLayout()
        withdrawInventory()

        if (!script.status.needsRepair) {
            script.status.isGeared = true
        }
        Bank.close()
    }

    private fun ensureCorrectGearEquipped() {
        // Loop through the gear list and check if each item is equipped.
        // If any item is missing, withdraw it from the bank and equip it.
        var itemsToEquip: Array<String> = arrayOf()
        var i = 0

        if (!script.configuration.useMagic) {
            primarySetup = script.configuration.rangeSetup
        }

        if (!script.configuration.useRange) {
            secondaryLoadOut = listOf()
        }

        for (gearItem in primarySetup) {
            if (Equipment.stream().name(gearItem.name()).isEmpty()) {
                if (Bank.withdraw(gearItem, 1)) {
                    Condition.wait { Inventory.stream().contains(gearItem) }
                    itemsToEquip[i] = gearItem.name()
                    i += 1
                } else {
                    // STOP SCRIPT MISSING ITEM
                    Notifications.showNotification("Stopping script because missing ${gearItem.name()}")
                    ScriptManager.stop()
                }

            }
        }

        if (itemsToEquip.isNotEmpty()) {
            val equipItems = Inventory.stream().name(*itemsToEquip).toList()
            for (item in equipItems) {
                item.interact("Wield", "Wear")
            }
        }
    }

    private fun getRingWithLowestCharges(): Item? {
        val allRings = mutableListOf<Item>()

        // Search for rings in the inventory and add them to the list
        allRings.addAll(Inventory.stream().nameContains("Ring of dueling").toList())

        // Search for rings in the bank and add them to the list
        Bank.stream().nameContains("Ring of dueling").forEach { item ->
            allRings.add(item)
        }

        // Iterate through all rings and find the one with the lowest charges (at least 2 charges)
        var ringWithLowestCharges: Item? = null
        var lowestCharges = Int.MAX_VALUE
        val pattern = Regex("""Ring of dueling\((\d+)\)""")

        for (ring in allRings) {
            val ringName = ring.name()
            val matchResult = pattern.find(ringName)

            if (matchResult != null) {
                val charges = matchResult.groupValues[1].toInt()
                if (charges in 3 until lowestCharges) {
                    lowestCharges = charges
                    ringWithLowestCharges = ring
                }
            }
        }

        return ringWithLowestCharges
    }

//    private fun ensureEnoughFood(): Boolean {
//        val bankItem = Bank.stream().name(script.configuration.foodName).first()
//        val foodCount = Inventory.stream().name(script.configuration.foodName).count(false).toInt()
//        val prayPotCount = Inventory.stream().name(PRAY_POT).count(false).toInt()
//        val karambwanCount = Inventory.stream().name(KARAMBWAN).count(false).toInt()
//        val magicBoostAmount = Inventory.stream().name(script.configuration.magicBoost).count(true).toInt()
//        val rangeBoostAmount = Inventory.stream().name(script.configuration.rangeBoost).count(true).toInt()
//        val antiVenomAmount = Inventory.stream().nameContains(ANTI_VENOM).count(true).toInt()
//
//        if (bankItem.stackSize() + foodCount < 20) {
//            Notifications.showNotification("Stopping script because out of ${script.configuration.foodName}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        if (prayPotCount + Bank.stream().name(PRAY_POT).count(true) < script.configuration.prayerPotAmount) {
//            logger.info("PPot Count${prayPotCount + Bank.stream().name(PRAY_POT).count()}")
//            Notifications.showNotification("Stopping script because out of ${PRAY_POT}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        if (karambwanCount + Bank.stream().name(KARAMBWAN).count(true) < script.configuration.karamAmount) {
//            logger.info("karam Count${karambwanCount + Bank.stream().name(KARAMBWAN).count(true)}")
//            Notifications.showNotification("Stopping script because out of ${KARAMBWAN}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        if (magicBoostAmount + Bank.stream().nameContains(script.configuration.magicBoost).toList().count() < 1) {
//            Notifications.showNotification("Stopping script because out of ${script.configuration.magicBoost}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        if (rangeBoostAmount + Bank.stream().nameContains(script.configuration.rangeBoost).toList().count() < 1) {
//            Notifications.showNotification("Stopping script because out of ${script.configuration.rangeBoost}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        if (antiVenomAmount + Bank.stream().nameContains(ANTI_VENOM).count(true) < 1) {
//            Notifications.showNotification("Stopping script because out of ${ANTI_VENOM}s")
//            ScriptManager.stop()
//            return false
//        }
//
//        return true
//    }

    private fun withdrawInventory() {
        val rangeNames = secondaryLoadOut.map { it.name() }.toTypedArray()
        val invenItems = Inventory.stream().filter { item ->
            ((item.name().contains(script.configuration.magicBoostPotion)&& item.stackSize() > 2) ||
                    (item.name().contains(script.configuration.rangeBoostPotion)&& item.stackSize() > 2) ||
                    (item.name().contains(ANTI_VENOM)&& item.stackSize() > 3)) ||
                    item.name() == KARAMBWAN ||
                    item.name() == PRAY_POT ||
                    item.name() == script.configuration.foodName
        }

        // we assume we already have the gear in the inventory/equipped
        if (Bank.depositAllExcept(
                *rangeNames,
                *invenItems.map { it.name() }.toTypedArray())) {
            logger.info("IDEAL LAYOUT ${invenItems.map { it.name() }.toTypedArray()}")
            for ((item, amount) in idealLayout) {
                val amountRemaining = amount - Inventory.stream().name(item).count(false).toInt()
                if (amountRemaining > 0) {
                    if (item == ANTI_VENOM || item == script.configuration.magicBoostPotion || item == script.configuration.rangeBoostPotion) {
                        val anti = Bank.stream().nameContains(item).first { it.stackSize() > 3 }
                        if (!Bank.withdraw(anti, amountRemaining)) {
                            Notifications.showNotification("Stopping script because out of ${item}s")
                            ScriptManager.stop()
                        }
                    } else {
                        if (!Bank.withdraw(item, amountRemaining)) {
                            Notifications.showNotification("Stopping script because out of ${item}s")
                            ScriptManager.stop()
                        }
                    }
                } else if (amountRemaining < 0) {
                    Bank.deposit(item, abs(amountRemaining))
                }
            }
        }
    }

    private fun buildIdealInventoryLayout(): Map<String, Int> {
        val layout = mutableMapOf<String, Int>()
        val secondary = secondaryLoadOut.map { it.name() }
        val primary = primarySetup.map { it.name() }
        val subtracted = secondary.filter { it !in primary }

        val inventory = Inventory.stream()

        val foodCount = inventory.name(script.configuration.foodName).count(false).toInt()
        val prayPotCount = inventory.name(PRAY_POT).count(false).toInt()
        val karambwanCount = inventory.name(KARAMBWAN).count(false).toInt()
//        val magicBoostAmount = inventory.name(script.configuration.magicBoost).count(true).toInt()
//        val rangeBoostAmount = inventory.name(script.configuration.rangeBoost).count(true).toInt()
//        val antiVenomAmount = inventory.nameContains(ANTI_VENOM).count(true).toInt()

        // Add secondaryItems to the layout
        logger.info("SECONDARY LOADOUT: ${secondary}, \n" +
                "FILTERED: $subtracted \n" +
                "PRIMARY LOADOUT: ${primarySetup.map { it.name() }}")
        for (item in subtracted) {
            logger.info("ADDING RANGE ITEM TO INVEN: $item")
            layout[item] = 1
        }

        // Add teleportMethod to the layout
        // get lowest charge r   ing above 2 charges
        layout[PRAY_POT] = script.configuration.prayerPotAmount
        layout[script.configuration.magicBoostPotion] = 1 // need to update these amounts remaining based on doses
        layout[script.configuration.rangeBoostPotion] = 1
        layout[ANTI_VENOM] = 1
        layout[KARAMBWAN] = script.configuration.karamAmount

        if (primary.contains(RING_OF_RECOIL)) {
            layout[RING_OF_RECOIL] = 1
            script.status.needsRepair = true
        }

        getRingWithLowestCharges()?.name()?.let {
            layout[it] = 1
        } ?: run {
            Notifications.showNotification("No more Ring of dueling")
            ScriptManager.stop()

        }
        layout[script.configuration.teleMethod] = 1

        layout[script.configuration.foodName] = 28 - layout.values.sumOf { it }

        return layout
    }
}