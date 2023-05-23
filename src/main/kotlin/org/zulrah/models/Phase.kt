package org.zulrah.models

import com.snow.zulrah.Script
import org.powbot.api.Tile
import org.powbot.api.rt4.Prayer

class Phase(val script: Script,
            val phaseNumber: Int,
            val tile: Tile,
            val pray: Prayer.Effect?,
            val transition: Tile?) {


    fun getPrayerBoost(): Prayer.Effect? {
        val rangeBoost = script.configuration.rangeBoost
        val mageBoost = script.configuration.magicBoost

        return when (pray) {
            Prayer.Effect.PROTECT_FROM_MAGIC -> if (script.configuration.useRange) rangeBoost else mageBoost
            Prayer.Effect.PROTECT_FROM_MISSILES -> if (script.configuration.useMagic) mageBoost else rangeBoost
            else -> null
        }
    }

}