package screech

import arc.Core
import arc.audio.Sound
import arc.files.Fi
import arc.struct.Seq
import arc.util.ArcRuntimeException
import arc.util.Log
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.entities.bullet.BulletType
import mindustry.gen.Sounds
import mindustry.type.*
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.Turret
import java.lang.reflect.Field
import com.github.mnemotechnician.mkui.delegates.setting

@Suppress("MemberVisibilityCanBePrivate")
object CVars {
    val syn = "cacophony-"
    val sounds = Seq<Sound>()

    var randomizeBlockSounds by setting(true, syn)
    //lets... not overwhelm them
    var randomizeBulletSounds by setting(false, syn)
    var randomizeUnitSounds by setting(false, syn)
    var randomizeWeaponSounds by setting(false, syn)


    private fun trySoundInit(file: Fi): Sound?{
        var sfx: Sound? = null

        try{
            sfx = Sound(file)
        }catch(e: ArcRuntimeException){
            Log.err("Failed to load sound file: ${file.name()}")
        }

        if(sfx != null) Log.info("Loaded sound: ${file.name()}")

        return sfx
    }

    fun loadSounds(){
        Sounds::class.java.declaredFields.forEach {
            try {
                if(it.type == Sound::class.java){
                    sounds.add(it.get(null) as Sound)
                }
            }catch(_: Exception){}
        }

        //extract sounds from other content
        getSoundFields<Content> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.each{everything ->
                    sounds.add(fi.get(everything) as Sound)
                }
            }catch(_: Exception){}
        }
    }

    fun loadCustomSounds(){
        val dir = Core.settings.dataDirectory.child("cacophony")

        if(!dir.exists()) dir.mkdirs()

        dir.walk{
            when{
                it.extension().equals("ogg") -> {
                    val sfx = trySoundInit(it)

                    if(sfx != null) sounds.add(sfx)
                }
                it.extension().equals("mp3") -> {
                    val sfx = trySoundInit(it)

                    if(sfx != null) sounds.add(sfx)
                }
                else -> Log.warn("Unknown file: ${it.name()}")
            }
        }
    }

    fun begin(){
        if(randomizeBlockSounds) getSoundFields<Block> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.blocks().each<Block>({ it is Block }){
                    fi.set(it, sounds.random())
                }
            }catch(_: Exception){}
        }

        if(randomizeBulletSounds) getSoundFields<BulletType> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.bullets().each{bul ->
                    fi.set(bul, sounds.random())
                }
            }catch(_: Exception){}
        }

        if(randomizeUnitSounds) getSoundFields<UnitType> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.units().each{unit ->
                    fi.set(unit, sounds.random())
                }
            }catch(_: Exception){}
        }

        if(randomizeWeaponSounds) getSoundFields<Weapon> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.units().each{unit ->
                    unit.weapons.each{
                        fi.set(it, sounds.random())
                    }
                }
            }catch(_: Exception){}
        }
    }

    private inline fun <reified T> getSoundFields(cons: (Field) -> Unit){
        T::class.java.declaredFields.forEach {
            if(it.type == Sound::class.java) cons(it)
        }
    }

}