package com.snow.zulrah.leafs

import com.snow.zulrah.OpenWintertodtConstants.ACTION_FEED
import com.snow.zulrah.OpenWintertodtConstants.ACTION_FIX
import com.snow.zulrah.OpenWintertodtConstants.ACTION_LIGHT
import com.snow.zulrah.OpenWintertodtConstants.ITEM_BRUMA_KINDLING
import com.snow.zulrah.OpenWintertodtConstants.ITEM_BRUMA_ROOT
import com.snow.zulrah.OpenWintertodtConstants.MESSAGES_BROKEN_EVENT
import com.snow.zulrah.extensions.Conditions
import com.snow.zulrah.extensions.count
import com.snow.zulrah.helpers.MessageListener
import com.snow.zulrah.helpers.SystemMessageManager
import com.snow.zulrah.models.WintertodtLocation
import org.powbot.api.Condition
import org.powbot.api.Random
import org.powbot.api.Tile
import org.powbot.api.rt4.*
import org.powbot.api.script.tree.Leaf
import java.util.logging.Logger

class LightingBrazier(script: com.snow.zulrah.Script) : Leaf<com.snow.zulrah.Script>(script, "Lighting brazier") {
    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun execute() {
        val brazier = getBrazier()
        if (brazier == GameObject.Nil || !brazier.inViewport()) {
            walkToBrazier()
            return
        }
        lightBrazier(brazier)
    }

    private fun lightBrazier(brazier: GameObject) {
        val action = getBrazierAction(brazier)
        val distance = Players.local().tile().distanceTo(brazier.tile)
        if (action != ACTION_FEED && distance > 2.5) {
            walkToBrazier()
            return
        }

        if (brazier.interact(action)) {
            if (distance > 4) {
                Condition.wait({ Players.local().inMotion() }, 50, 20)
                Condition.wait({ !Players.local().inMotion() }, 100, 20)
            }
            val initialHp = Combat.health()

            if (Condition.wait({ Combat.health() < initialHp || Players.local().animation() != -1 }, 100, 20)) {
                when (action) {
                    ACTION_FEED -> waitForLighting(initialHp)
                    ACTION_FIX -> Condition.wait(Conditions.expGained(Constants.SKILLS_CONSTRUCTION))
                    ACTION_LIGHT -> Condition.wait(Conditions.expGained(Constants.SKILLS_FIREMAKING))
                }
            }
        }
    }

    private fun waitForLighting(startingHp: Int): Boolean {
        val waitTimer = MessageListener(1, MESSAGES_BROKEN_EVENT, 120000)
        SystemMessageManager.addMessageToListen(waitTimer)
        val times = Inventory.count(ITEM_BRUMA_KINDLING, ITEM_BRUMA_ROOT)
        for (i in 0..times) {
            val exp = Skills.experience(Constants.SKILLS_FIREMAKING)

            val success = Condition.wait(
                {
                    exp < Skills.experience(Constants.SKILLS_FIREMAKING) || waitTimer.count == 0
                },
                300,
                12
            )

            if (waitTimer.count == 0 || Combat.health() < startingHp || !success) {
                break
            }
        }
        return true
    }

    private fun getBrazierAction(brazier: GameObject): String {
        return when {
            brazier.id() == 29313 -> ACTION_FIX
            brazier.id() == 29312 -> ACTION_LIGHT
            else -> ACTION_FEED
        }
    }

    private fun getBrazier(): GameObject {
        return Objects.stream(/*script.status.currentLocation.brazierTile, 4, GameObject.Type.INTERACTIVE*/)
            .name(com.snow.zulrah.OpenWintertodtConstants.OBJECT_BURNING_BRAZIER, com.snow.zulrah.OpenWintertodtConstants.OBJECT_BRAZIER)
            .nearest()
            .first()
    }

    private fun walkToBrazier() {
        val tile = Tile(
            Random.nextInt(
                WintertodtLocation.SOUTH_WEST.minBrazierX,
                WintertodtLocation.SOUTH_WEST.maxBrazierX
            ), WintertodtLocation.SOUTH_WEST.brazierY
        )
        val result = Movement.builder(tile)
            .setWalkUntil { tile.distance() < 2.5 && tile.matrix().inViewport() }
            .move()

        if (!result.success) {
            logger.info("Success failed, ${result.failureReason}, Used web ${result.usedWeb}")
        }
    }
}