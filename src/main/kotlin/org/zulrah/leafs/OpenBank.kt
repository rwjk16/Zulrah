package org.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.BANK_TODT
import com.snow.zulrah.extensions.nearestGameObject
import org.Constants.Companion.FEROX_BANK
import org.Constants.Companion.FEROX_ENCLAVE_X
import org.Constants.Companion.FEROX_ENCLAVE_Y
import org.Constants.Companion.RING_DUELING
import org.powbot.api.Condition
import org.powbot.api.rt4.*
import org.powbot.api.script.tree.Leaf

class OpenBank(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Opening bank") {
    override fun execute() {
        val bankChest = Objects.nearestGameObject("Bank chest")
//        val playerTile = Players.local().tile()

//        if (playerTile.x in FEROX_ENCLAVE_X && playerTile.y in FEROX_ENCLAVE_Y) {
            if (bankChest.inViewport(true) && bankChest.interact("Use")) {
                Condition.wait ({ Players.local().inMotion() || Bank.opened() }, 500, 5)
                Condition.wait({ !Players.local().inMotion() || Bank.opened() }, 500, 15)
            } else {
                Movement.builder(FEROX_BANK)
                    .setWalkUntil { FEROX_BANK.matrix().inViewport(true)}
                    .move()
                if (FEROX_BANK.distance() > 5) {
                    Movement.step(FEROX_BANK, 1)
                }
            }
//        }
    }

    private fun teleOut(): Boolean {
        val ring = Equipment.itemAt(Equipment.Slot.RING)

        if (ring.name().contains(RING_DUELING, true)) {
            if (Equipment.open()) {
                ring.interact("Ferox Enclave") // TODO: get actual tele name
                return true
            }
        }
        return false
    }
}