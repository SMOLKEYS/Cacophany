package screech

import arc.*
import mindustry.mod.*
import mindustry.game.*

class Cacophony : Mod() {

	init {
		Events.on(EventType.ClientLoadEvent::class.java) {
			CSettings.load()
		}
		Events.on(EventType.ContentInitEvent::class.java) {
			CVars.loadSounds()
			CVars.begin()
		}
	}

}
