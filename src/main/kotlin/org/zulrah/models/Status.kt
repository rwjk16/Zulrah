package org.zulrah.models

import com.snow.zulrah.Script
import org.Constants
import org.powbot.api.Notifications
import org.powbot.api.rt4.Prayer
import org.powbot.mobile.script.ScriptManager

class Status(val script: Script) {
    var currentZulrahPhase: Phase = Phase(
        script = script,
        phaseNumber = 0,
        pray = null,
        tile = Constants.START_TILE,
        transition = null
    )
    var currentZulrahRotation: Int = 0
    var reset = true
    var needsRepair = false
    var isGeared = false

    var error: Error? = null
        set(value) {
            field = value
            if (value != null) {
                Notifications.showNotification(value.message!!)
                ScriptManager.stop()
            }
        }
}