package org.polyfrost.polyhitbox.commands

import cc.polyfrost.oneconfig.utils.NetworkUtils
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import org.polyfrost.polyhitbox.PolyHitbox
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import org.polyfrost.polyhitbox.config.ModConfig

@Command(value = "h")
class ModCommand {

    @Main
    fun main() {
        if(ModConfig.playerNames.isEmpty()) {
            UChat.chat("§b[Hitboxes+] §cFriends list is empty!")
            return
        }
        UChat.chat("§9----------------------------")
        for(player in ModConfig.playerNames) UChat.chat("§e" + player)
        UChat.chat("§9----------------------------")
    }

    @Main
    fun main(player: String) {
        if(player.lowercase() == "clear"){
            ModConfig.list.clear()
            ModConfig.playerNames.clear()
            UChat.chat("§b[Hitboxes+] §cCleared friends list.")
            return
        } else if(player.lowercase() == "list"){
            if(ModConfig.playerNames.isEmpty()) {
                UChat.chat("§b[Hitboxes+] §cFriends list is empty!")
                return
            }
            UChat.chat("§9----------------------------")
            for(player in ModConfig.playerNames) UChat.chat("§e" + player)
            UChat.chat("§9----------------------------")
            return
        }
        Multithreading.runAsync {
            try {
                if(ModConfig.playerNames.contains(player.lowercase())) {
                    UChat.chat("§b[Hitboxes+] §c$player is already in friends list.")
                } else {
                    ModConfig.list.add(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                    UChat.chat("§b[Hitboxes+] §aAdded $player to friends list.")
                    ModConfig.playerNames.add(player)
                }
            } catch (_: Exception) {
                UChat.chat("§b[Hitboxes+] §cPlayer not found.")
            }
        }
    }

    @SubCommand
    fun add(player: String) {
        Multithreading.runAsync {
            try {
                if(ModConfig.playerNames.contains(player.lowercase())) {
                    UChat.chat("§b[Hitboxes+] §c$player is already in friends list.")
                } else {
                    ModConfig.list.add(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                    UChat.chat("§b[Hitboxes+] §aAdded $player to friends list.")
                    ModConfig.playerNames.add(player)
                }
            } catch (_: Exception) {
                UChat.chat("§b[Hitboxes+] §cPlayer not found.")
            }
        }
    }

    @SubCommand
    fun remove(player: String) {
        Multithreading.runAsync {
            try {
                ModConfig.list.remove(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                UChat.chat("§b[Hitboxes+] §cRemoved $player to friends list.")
                ModConfig.playerNames.remove(player.lowercase())
            } catch (_: Exception) {
                UChat.chat("§b[Hitboxes+] §cPlayer not found.")
            }
        }
    }
}