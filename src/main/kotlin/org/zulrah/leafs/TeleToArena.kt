package com.snow.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.ACTION_ENTER
import com.snow.zulrah.OpenWintertodtConstants.AREA_INSIDE_ARENA
import com.snow.zulrah.OpenWintertodtConstants.OBJECT_DOOR
import com.snow.zulrah.OpenWintertodtConstants.TILE_NEAR_DOOR_OUTSIDE
import com.snow.zulrah.Script
import org.Constants
import org.Constants.Companion.RING_DUELING
import org.powbot.api.Condition
import org.powbot.api.Notifications
import org.powbot.api.Tile
import org.powbot.api.rt4.*
import org.powbot.api.rt4.walking.local.LocalPathFinder
import org.powbot.api.script.tree.Leaf
import org.powbot.mobile.script.ScriptManager
import org.zulrah.models.TeleMethod

class TeleToArena(script: Script) : Leaf<Script>(script, "Teleing To arena") {
    override fun execute() {
        if (Bank.opened()) {
            Bank.close()
            return
        }

        when (script.configuration.teleMethod) {
            TeleMethod.DRAMEN, TeleMethod.LUNAR -> handleFairRing()
            TeleMethod.ZUL -> handleZulTele()
            TeleMethod.CATHERBY -> handleCatherbyTele()
        }
    }

    private fun handleFairRing() {
        // here we will use the ring of dueling to tele to castle wars
        val ring = Inventory.stream().nameContains(RING_DUELING).first()
        if (ring != null) {
            if (ring.interact(Constants.WEAR)) {
                Equipment.open()
                val tile = Players.local().tile()
                Equipment.stream().nameContains(RING_DUELING).first().interact("Castle Wars")
                Condition.wait { Players.local().tile() != tile } // changed tile
            } else {
                Notifications.showNotification("Cant Find Ring of dueling")
                ScriptManager.stop()
            }
        } else {
            Notifications.showNotification("Cant find Ring of dueling")
            ScriptManager.stop()
        }
        // then walk to the fairy ring - in a seperate leaf?

        // then we will tele Zul

        // hop across the stone

        // enter arena

    }

    private fun handleZulTele() {
        // here we will break the tele scroll
        val tele = Inventory.stream().name(TeleMethod.ZUL).first()

        if (tele != null) {
            if (tele.click()) {
            }
            Notifications.showNotification("Cant Find ${TeleMethod.ZUL}")
            ScriptManager.stop()
        }

        // enter arena

    }

    private fun handleCatherbyTele() {
        // here we will break the tele tab
        val tele = Inventory.stream().name(TeleMethod.CATHERBY).first()

        if (tele != null) {
            val tile = Players.local().tile()
            if (tele.click()) {
                //
                Condition.wait { Players.local().tile() != tile }
                // walk towards charter ship

                // interact with charter - select tyras?
            }
            Notifications.showNotification("Cant Find ${TeleMethod.CATHERBY}")
            ScriptManager.stop()
        }

    }
}