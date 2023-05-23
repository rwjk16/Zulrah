package org.tasks

import org.zulrah.models.Configuration
import org.powbot.api.rt4.Inventory

class BossTask(
    private val config: Configuration
) : BaseTask() {

    // we will need to store the possible phases, and where to stand for each
    // each phase will contain which to pray, and gear will be changed accordingly


    override fun shouldExcecute(): Boolean {
        // should only run when inside zulrah room
        return Inventory.stream().count().toInt() != 28 &&
                Inventory.stream().name(config.foodName).isNotEmpty()
    }

    override fun run() {
        // here we need to
        // check the current phase
        // move to tile
        // change prayer
        // equip gear based on phase
        // attack
        // wait until either next step or move when smoke is gone
        // loot
//        when(true) {
//            Constants.insideBankArea.contains(Players.local().tile()) -> leaveBank()
//            Constants.insideHouseContainsPlayer() -> pickPocket()
//            Constants.outsideBankArea.contains(Players.local().tile()) -> enterHouse()
//            Constants.outsideHouseArea.contains(Npcs.stream().name(Constants.bitch).nearest().first()) -> hopWorlds()
//            // bitch outside - hop
//            else -> walkToHouse()
//        }
    }
}