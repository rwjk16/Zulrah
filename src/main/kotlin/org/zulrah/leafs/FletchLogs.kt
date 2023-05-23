package com.snow.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.ITEM_BRUMA_KINDLING
import com.snow.zulrah.OpenWintertodtConstants.ITEM_BRUMA_ROOT
import com.snow.zulrah.OpenWintertodtConstants.TOOL_KNIFE
import com.snow.zulrah.extensions.count
import com.snow.zulrah.helpers.CommonMethods
import com.snow.zulrah.helpers.InteractionsHelper
import org.powbot.api.Condition
import org.powbot.api.rt4.Chat
import org.powbot.api.rt4.Combat
import org.powbot.api.rt4.Inventory
import org.powbot.api.script.tree.Leaf
import java.util.logging.Logger

class FletchLogs(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Fletching logs") {
    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun execute() {
        if (Chat.chatting() && !Chat.completeChat()) {
            logger.info("Exiting failed to chat")
            return
        }

        val knife = Inventory.stream().name(TOOL_KNIFE).first()
        val log = Inventory.stream().name(ITEM_BRUMA_ROOT).toList()
        if (log.isEmpty()) {
            logger.info("No logs found")
            return
        }

        val useResult = InteractionsHelper.useItemOn(knife, log.first())
        if (!useResult) {
            logger.info("Failed to use item")
            return
        }

        var currentLogCount = log.size
        var kindlingCount: Int
        val startingHp = Combat.health()
        for (i in 0..log.size) {
            var shouldExit = false
            val result = Condition.wait({
                val oldLogCount = currentLogCount

                currentLogCount = Inventory.stream().name(ITEM_BRUMA_ROOT).count().toInt()
                kindlingCount = Inventory.count(ITEM_BRUMA_KINDLING)

                if (shouldExit(currentLogCount, kindlingCount, startingHp)) {
                    shouldExit = true
                    return@wait true
                }

                currentLogCount < oldLogCount || currentLogCount == 0
            }, 500, 6)

            if (shouldExit || !result) {
                return
            }
        }
    }

    private fun shouldExit(logCount: Int, kindlingCount: Int, startingHp: Int): Boolean {
        return CommonMethods.remainingHealthPercentage() <= logCount + kindlingCount || Chat.canContinue()
                || Combat.health() < startingHp
    }
}