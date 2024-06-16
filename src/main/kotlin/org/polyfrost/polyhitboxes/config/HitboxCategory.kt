package org.polyfrost.polyhitboxes.config

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityHanging
import net.minecraft.entity.IProjectile
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.entity.projectile.EntityFireball
import org.polyfrost.polyhitboxes.render.DummyWorld

private const val HIGH = 0
private const val MID = 1
private const val LOW = 2

enum class HitboxCategory(
    val displayName: String,
    val condition: (Entity) -> Boolean,
    val priority: Int = MID,
    val example: Entity? = null,
    var config: HitboxConfig = HitboxConfig(),
) {
    DEFAULT(
        displayName = "General",
        condition = { true },
        priority = LOW
    ),
    PLAYER(
        displayName = "Player",
        condition = { it is EntityPlayer }
    ),
    SELF(
        displayName = "Self",
        condition = { it is EntityPlayerSP && it.uniqueID == mc.thePlayer?.uniqueID },
        priority = HIGH
    ),
    ARROW(
        displayName = "Arrow",
        condition = { it is EntityArrow },
        example = DummyWorld.ARROW,
        priority = HIGH
    ),
    FIREBALL(
        displayName = "Fireball",
        condition = { it is EntityFireball },
        example = DummyWorld.FIREBALL
    ),
    PROJECTILE(
        displayName = "Projectile",
        condition = { it is IProjectile },
        example = DummyWorld.SNOWBALL
    ),
    DECORATION(
        displayName = "Decoration",
        condition = { it is EntityHanging },
        example = DummyWorld.ITEM_FRAME,
    ),
    ARMOR_STAND(
        displayName = "Armor Stand",
        condition = { it is EntityArmorStand },
        example = DummyWorld.ARMOR_STAND,
    ),
    ITEM(
        displayName = "Item",
        condition = { it is EntityItem },
        example = DummyWorld.ITEM,
    ),
}
