package com.snow.zulrah

import com.google.common.eventbus.Subscribe
import org.zulrah.branch.IsInside
import com.snow.zulrah.helpers.SystemMessageManager
import org.zulrah.models.Configuration
import org.zulrah.models.Status
import org.powbot.api.Notifications
import org.powbot.api.event.BreakEvent
import org.powbot.api.event.MessageEvent
import org.powbot.api.rt4.Equipment
import org.powbot.api.rt4.Item
import org.powbot.api.rt4.walking.model.Skill
import org.powbot.api.script.*
import org.powbot.api.script.paint.Paint
import org.powbot.api.script.paint.PaintBuilder
import org.powbot.api.script.tree.TreeComponent
import org.powbot.api.script.tree.TreeScript
import org.powbot.mobile.script.ScriptManager
import org.powbot.mobile.service.ScriptUploader
import org.zulrah.models.TeleMethod
import java.util.logging.Logger

@ScriptManifest(
    name = "Tester",
    description = "Kill bitch snek for its shit",
    version = "1.0.4",
    category = ScriptCategory.MoneyMaking,
    author = "SNOW",
    markdownFileName = ""
)
@ScriptConfiguration.List(
    [
        ScriptConfiguration(
            name = "Food",
            description = "Pick type of food to eat",
            allowedValues = ["Manta ray", "Shark", "Monkfish"],
            defaultValue = "Manta ray"
        ),

        ScriptConfiguration(
            name = "Karambwan amount",
            description = "Pick amount of karambwans to bring",
            defaultValue = "4",
            optionType = OptionType.INTEGER
        ),

        ScriptConfiguration(
            name = "Prayer potion amount",
            description = "Choose how many prayer pots to bring",
            defaultValue = "3",
            optionType = OptionType.INTEGER
        ),

        ScriptConfiguration(
            name = "Use magic",
            description = "Use magic?",
            defaultValue = "false",
            optionType = OptionType.BOOLEAN,
        ),

        ScriptConfiguration(
            name = "Mage setup",
            description = "Equip gear used for magic switch, if necessary",
            optionType = OptionType.EQUIPMENT,
            visible = false,
        ),

        ScriptConfiguration(
            name = "Magic boost",
            description = "Pick type of boost to use for magic",
            allowedValues = ["Imbued heart", "Ancient potion", "Divine magic potion", "Magic potion"],
            defaultValue = "Magic potion",
            visible = false,
        ),

        ScriptConfiguration(
            name = "Use range",
            description = "Use range?",
            defaultValue = "false",
            optionType = OptionType.BOOLEAN,
        ),

        ScriptConfiguration(
            name = "Range setup",
            description = "Equip gear used for range switch",
            optionType = OptionType.EQUIPMENT,
            visible = false,
        ),

        ScriptConfiguration(
            name = "Range boost",
            description = "Pick type of boost to use for range",
            allowedValues = ["Divine Ranging potion", "Ranging potion"],
            defaultValue = "Ranging potion",
            visible = false,
        ),

        ScriptConfiguration(
            name = "Tele method",
            description = "Pick how you want to get to zulrah",
            allowedValues = [TeleMethod.ZUL, TeleMethod.DRAMEN, TeleMethod.LUNAR, TeleMethod.CATHERBY],
            defaultValue = "Dramen staff"
        ),
//
//        ScriptConfiguration(
//            name = "Upgrade gear",
//            description = "upgrade gear when enough money available?",
//            optionType = OptionType.BOOLEAN
//        )
    ]
)
class Script : TreeScript() {

    override val rootComponent: TreeComponent<*> by lazy {
        IsInside(this)
    }

    lateinit var configuration: Configuration
    lateinit var status: Status
    private var rangeSetup: List<Item> = listOf()
    private var mageSetup: List<Item> = listOf()

    private var oldM: Map<Int, Int> = mapOf()
    private var oldR: Map<Int, Int> = mapOf()

    private var logger: Logger = Logger.getLogger(this.javaClass.simpleName)

    override fun onStart() {
        val foodName: String = getOption("Food")
//        val rangeSetup: LinkedHashMap<Int, Int> = getOption("Range setup")
//        val magicSetup: LinkedHashMap<Int, Int> = getOption("Mage setup")
        val prayAmount: Int = getOption("Prayer potion amount")
        val karamAmount: Int = getOption("Karambwan amount")
        val magicBoost: String = getOption("Magic boost")
        val rangeBoost: String = getOption("Range boost")
        var teleMethod: String = getOption("Tele method")
        val useMagic: Boolean = getOption("Use magic")
        val useRange: Boolean = getOption("Use range")



        if (mageSetup == rangeSetup) {
            Notifications.showNotification("Range setup cannot be the same as mage setup")
            ScriptManager.stop()
        }

        configuration = Configuration(
            foodName = foodName,
            mageSetup = mageSetup,
            rangeSetup = rangeSetup,
            prayerPotAmount = prayAmount,
            karamAmount = karamAmount,
            rangeBoostPotion = rangeBoost,
            magicBoostPotion = magicBoost,
            useMagic = useMagic,
            useRange = useRange,
            teleMethod = teleMethod
        )

        status = Status()
        addPaint()
    }

    private fun addPaint() {
        val p: Paint = PaintBuilder.newBuilder()
            .addString("Last leaf:") { lastLeaf.name }
            .trackSkill(Skill.Magic)
            .trackSkill(Skill.Ranged)
            .trackSkill(Skill.Hitpoints)
            .trackSkill(Skill.Defence)
//            .trackInventoryItems(20703) // zulrah loot
            .y(45)
            .x(40)
            .build()
        addPaint(p)
    }

    @ValueChanged("Use magic")
    fun magicUpdated(updatedValue: Boolean) {
        updateVisibility("Mage setup", updatedValue)
        updateVisibility("Magic boost", updatedValue)
        logger.info("UPDATED VALUE: $updatedValue")

    }

    @ValueChanged("Use range")
    fun rangeUpdated(updatedValue: Boolean) {
        updateVisibility("Range setup", updatedValue)
        updateVisibility("Range boost", updatedValue)
    }

    @ValueChanged("Range setup")
    fun rangeSetupUpdated(updatedValue: Map<Int, Int>) {
        logger.info("RANGE SETUP UPDATED")
        if (oldM != updatedValue && oldR != updatedValue) {
            rangeSetup = Equipment.get()
            logger.info("RANGE SETUP: ${rangeSetup.map { it.name() }}")
            oldR = updatedValue
        }
    }

    @ValueChanged("Mage setup")
    fun magicSetupUpdated(updatedValue: Map<Int, Int>) {
        logger.info("MAGE SETUP UPDATED")
        if (oldM != updatedValue && oldR != updatedValue) {
            mageSetup = Equipment.get()
            logger.info("MAGE SETUP: ${mageSetup.map { it.name() }}")
            oldM = updatedValue
        }
    }


    /**
     *  Subscribes to the messages received from the game and updates the status accordingly
     *
     *  @param messageEvent The message received form the game.
     */
    @Subscribe
    fun message(messageEvent: MessageEvent) {
        SystemMessageManager.messageRecieved(messageEvent)
    }

    /**
     *  This will only let the script break when its not in a game currently
     */
    @Subscribe
    fun breakEvent(breakEvent: BreakEvent) {
//        val gameRunning = Varpbits.varpbit(com.snow.zulrah.OpenWintertodtConstants.VARPBIT_RESPAWN) == 0
//        if (gameRunning) {
//            val remainingPercent = CommonMethods.remainingHealthPercentage()
//            val breakTime = if (remainingPercent < 5) Random.nextInt(5000, 8000) else remainingPercent * 1.5
//            breakEvent.delay(breakTime.toLong())
//        } else {
//            breakEvent.accept()
//        }
    }
}

fun main(args: Array<String>) {
    ScriptUploader().uploadAndStart("Tester", "", "127.0.0.1:5554", false, false)
}