package screech

import com.github.mnemotechnician.mkui.extensions.dsl.textButton
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.ui.dialogs.SettingsMenuDialog
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting

object CSettings {

    fun load(){
        Vars.ui.settings.addCategory("Cacophony", Icon.refresh){
            it.buttonPref("randomizesounds"){
                CVars.begin()
            }
        }
    }

    fun SettingsTable.buttonPref(name: String, onClick: () -> Unit) = this.pref(ButtonSetting(name, onClick))

    open class ButtonSetting(name: String, var onClick: () -> Unit): Setting(name){

        override fun add(table: SettingsTable) {
            table.textButton(title, wrap = false){
                onClick()
            }.growX().row()
        }
    }
}