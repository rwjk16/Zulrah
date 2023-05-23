package org.zulrah.models

import org.powbot.api.rt4.Item
import org.powbot.api.rt4.Prayer

class Configuration(
    val foodName: String,
    val mageSetup: List<Item>,
    val rangeSetup: List<Item>,
    val prayerPotAmount: Int,
    val karamAmount: Int,
    val rangeBoostPotion: String?,
    val magicBoostPotion: String?,
    val rangeBoost: Prayer.Effect?,
    val magicBoost: Prayer.Effect?,
    val useMagic: Boolean,
    val useRange: Boolean,
    val teleMethod: String
)