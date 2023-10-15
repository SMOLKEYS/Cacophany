package screech

import arc.audio.Sound
import arc.struct.Seq
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.entities.bullet.BulletType
import mindustry.gen.Sounds
import mindustry.type.*
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.Turret
import java.lang.reflect.Field

@Suppress("MemberVisibilityCanBePrivate")
object CVars {

    val sounds = Seq<Sound>()

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

    fun begin(){
        getSoundFields<Block> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.blocks().each<Block>({ it is Block }){
                    fi.set(it, sounds.random())
                }
            }catch(_: Exception){}
        }

        getSoundFields<BulletType> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.bullets().each{bul ->
                    fi.set(bul, sounds.random())
                }
            }catch(_: Exception){}
        }

        getSoundFields<UnitType> {fi ->
            try{
                fi.isAccessible = true
                Vars.content.units().each{unit ->
                    fi.set(unit, sounds.random())
                }
            }catch(_: Exception){}
        }

        getSoundFields<Weapon> {fi ->
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