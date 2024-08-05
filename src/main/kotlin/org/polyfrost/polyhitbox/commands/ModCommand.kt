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
        UChat.chat("§bFriends list:")
        for(player in ModConfig.playerNames) UChat.chat(player)
    }

    @Main
    fun main(player: String) {
        if(player.lowercase() == "clear"){
            ModConfig.list.clear()
            ModConfig.playerNames.clear()
            UChat.chat("Cleared friends list.")
            return
        } else if(player.lowercase() == "list"){
            UChat.chat("§bFriends list:")
            for(player in ModConfig.playerNames) UChat.chat(player)
            return
        }
        Multithreading.runAsync {
            try {
                if(ModConfig.playerNames.contains(player.lowercase())) {
                    UChat.chat("$player is already in friends list.")
                } else {
                    ModConfig.list.add(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                    UChat.chat("Added $player to friends list.")
                    ModConfig.playerNames.add(player)
                }
            } catch (_: Exception) {
                UChat.chat("Player not found.")
            }
        }
    }

    @SubCommand
    fun add(player: String) {
        Multithreading.runAsync {
            try {
                if(ModConfig.playerNames.contains(player.lowercase())) {
                    UChat.chat("$player is already in friends list.")
                } else {
                    ModConfig.list.add(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                    UChat.chat("Added $player to friends list.")
                    ModConfig.playerNames.add(player)
                }
            } catch (_: Exception) {
                UChat.chat("Player not found.")
            }
        }
    }

    @SubCommand
    fun remove(player: String) {
        Multithreading.runAsync {
            try {
                ModConfig.list.remove(NetworkUtils.getJsonElement("https://api.mojang.com/users/profiles/minecraft/$player").asJsonObject.get("id").asString)
                UChat.chat("Removed $player to friends list.")
                ModConfig.playerNames.remove(player.lowercase())
            } catch (_: Exception) {
                UChat.chat("Player not found.")
            }
        }
    }
}