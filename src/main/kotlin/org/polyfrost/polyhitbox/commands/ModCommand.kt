package org.polyfrost.polyhitbox.commands

import cc.polyfrost.oneconfig.utils.NetworkUtils
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import org.polyfrost.polyhitbox.PolyHitbox
import cc.polyfrost.oneconfig.libs.universal.UChat
import org.polyfrost.polyhitbox.render.HitboxRenderer

@Command(value = PolyHitbox.MODID)
class ModCommand {

    @SubCommand
    fun add(player: String) {
        Multithreading.runAsync {
            try {
                HitboxRenderer.list.add(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                UChat.chat("Added $player to friends list.")
                HitboxRenderer.playerNames.add(player)
            } catch (_: Exception) {
                UChat.chat("Player not found.")
            }
        }
    }

    @SubCommand
    fun remove(player: String) {
        Multithreading.runAsync {
            try {
                HitboxRenderer.list.remove(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                UChat.chat("Removed $player to friends list.")
                HitboxRenderer.playerNames.remove(player)
            } catch (_: Exception) {
                UChat.chat("Player not found.")
            }
        }
    }

    @SubCommand
    fun list() {
        UChat.chat("Friends list:")
        for(player in HitboxRenderer.playerNames) UChat.chat(player)
    }

    @SubCommand
    fun clear(){
        HitboxRenderer.list.clear()
        HitboxRenderer.playerNames.clear()
        UChat.chat("Cleared friends list.")
    }
}