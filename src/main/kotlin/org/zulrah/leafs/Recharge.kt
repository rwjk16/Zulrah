package org.zulrah.leafs

import com.snow.zulrah.Script
import org.Constants.Companion.BREAK
import org.Constants.Companion.BREAK_THE_RING
import org.Constants.Companion.RING_OF_RECOIL
import org.powbot.api.Condition
import org.powbot.api.rt4.Chat
import org.powbot.api.rt4.Inventory
import org.powbot.api.script.tree.Leaf
import java.util.logging.Logger

class Recharge(script: Script) : Leaf<Script>(script, "Handling Recharge") {

    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun execute() {
        val inventory = Inventory.get()

        (inventory.firstOrNull { it.name() == RING_OF_RECOIL })?.let {
            it.interact(BREAK)
            logger.info("CHAT OPTIONS: ${Chat.getChatMessage()}")
            Chat.completeChat(BREAK_THE_RING, "Continue")
            Condition.wait { !Chat.chatting() }
            script.status.needsRepair = false
        }


    }
}