package com.snow.zulrah.helpers

import com.snow.zulrah.OpenWintertodtConstants.TEXTURE_BURNING
import com.snow.zulrah.OpenWintertodtConstants.TEXTURE_PYROMANCERDEAD
import com.snow.zulrah.OpenWintertodtConstants.WIDGETS_ROOT_ID
import org.powbot.api.rt4.Component
import org.powbot.api.rt4.Equipment
import org.powbot.api.rt4.Widgets

object CommonMethods {
    private val energyPercentage = Regex(": (.*?)%")

    fun isPyromancerDead(location: WintertodtLocation): Boolean {
        return Widgets.component(WIDGETS_ROOT_ID, location.pyroComponentId).textureId() == TEXTURE_PYROMANCERDEAD
    }


    fun isBrazierAlive(location: WintertodtLocation): Boolean {
        return Widgets.component(WIDGETS_ROOT_ID, location.brazierComponentId).textureId() == TEXTURE_BURNING
    }

    fun remainingHealthPercentage(): Int {
        val energy = Widgets.component(WIDGETS_ROOT_ID, com.snow.zulrah.OpenWintertodtConstants.WIDGETS_ENERGY)

        if (energy == Component.Nil) {
            return 0
        }

        val energyText = energy.text()
        return try {
            val parsedText = energyPercentage.find(energyText)!!.groupValues[1]
            Integer.parseInt(parsedText)
        } catch (ex: Exception) {
            0
        }
    }
}