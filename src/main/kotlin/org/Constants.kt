package org
import org.powbot.api.Area
import org.powbot.api.Condition
import org.powbot.api.Tile
import org.powbot.api.rt4.Movement
import org.powbot.api.rt4.Players

class Constants {

    companion object {
        const val equip: String = "Wear"
        const val eat: String = "Eat"
        const val drink: String = "Drink"
        const val coinPouch: String = "Coin pouch"
        const val openAll = "Open-all"
        const val open = "Open"
        const val close = "Close"
        const val door = "Door"
        const val climbUp = "Climb-up"
        const val stairs = "Stairs"
        const val enter = "Enter"
        const val kBreak = "Break"
        const val climb = "Climb"
        const val revert = "Revert"
        const val climbDown = "Climb-down"
        const val crack = 26382
        const val hole = 26418
        const val firstRock = 26372
        const val crawlThrough = "Crawl-through"
        const val crystalHelm = "Crystal helm"
        const val crystalBody = "Crystal body"
        const val crystalLegs = "Crystal legs"
        const val bGloves = "Barrows gloves"
        const val bofa = "Bow of faerdhinen"
        const val anguish = "Necklace of anguish"
        const val avas = "Ava's assembler"
        const val archersRing = "Archers ring (i)"
        const val suffering = "Ring of suffering (ri)"
        const val saraBoots = "Saradomin d'hide boots"
        const val zammyBoots = "Zamorak d'hide boots"
        const val unholyBlessing = "Unholy blessing"
        const val holyBlessing = "Holy blessing"
        const val blowpipe = "Toxic Blowpipe"
        const val staminaPot = "Stamina potion"
        const val brews = "Saradomin brew (4)"
        const val shark = "Shark"
        const val restore = "Super restore (4)"
        const val bastion = "Bastion potion (4)"
        const val divineBastion = "Divine bastion potion (4)"
        const val rangingPotion = "Ranging potion (4)"
        const val trollheimTele = "Trollheim teleport"
        const val houseTele = "Teleport to house"
        const val bonesToPeaches = "Bones to peaches"
        const val scrollOfRedirection = "Scroll of redirection"
        const val KARAMBWAN = "Cooked karambwan"
        const val BREAK =  "Break"
        const val BREAK_THE_RING = "Break the ring."
        const val WEAR = "Wear"
        const val INSUFFICIENT = "Insufficient"
        val START_TILE = Tile(11882, 4326)

        val RING_DUELING = "Ring of Dueling"
        val RING_OF_RECOIL = "Ring of recoil"
        val RING_OF_SUFFERING = "Ring of suffering(i)"
        const val PRAY_POT = "Prayer potion(4)"
        val ZUL_INST_AREA = Area(
            Tile(11882, 4326),
            Tile(11882, 4316),
            Tile(118870, 4318),
            Tile(11870, 4326),)

        val FEROX_BANK = Tile(3130, 3631)
        val FEROX_ENCLAVE_X = 3123..3155 // Range of x-coordinates for the Ferox Enclave
        val FEROX_ENCLAVE_Y = 3617..3645
        val ANTI_VENOM = "Anti-venom+"

        fun waitUntilCloseEnough(distance: Int,
                                 freq: Int = 150,
                                 tries: Int = 10) {
            Condition.wait({ Players.local().tile().distanceTo(Movement.destination()) <= distance}, freq, tries)
        }
    }
}