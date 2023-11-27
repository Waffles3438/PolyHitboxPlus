package org.polyfrost.polyhitboxes.config

import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.elements.config.ConfigCheckbox
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.drawText
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IProjectile
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.boss.EntityWither
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.*
import kotlin.reflect.jvm.javaField

object HitboxMainTree { // todo: finish this
    var savedMap = HashMap<String, HitboxProfile>()
    val all = named("All") { true }.children(
        typed<EntityPlayer>("Player").children(
            named("Self") {
                it == UPlayer.getPlayer()
            },
            named("Same Team") {
                it is EntityLivingBase && UPlayer.getPlayer()?.isOnSameTeam(it) == true
            },
        ),
        typed<EntityLiving>("Mob").children(
            typed<EntityArmorStand>("ArmorStand"),
            typed<IMob>("Hostile").children(
                typed<EntityZombie>("Zombie"),
                typed<EntityCreeper>("Creeper"),
                named("Skeleton") { it is EntitySkeleton && it.skeletonType == 0 },
                typed<EntitySpider>("Spider"),
                typed<EntityCaveSpider>("Cave Spider"),
                typed<EntityWitch>("Witch"),
                typed<EntitySilverfish>("Silverfish"),
                typed<EntitySlime>("Slime"),
                typed<EntityGuardian>("Guardian"),
                typed<EntityPigZombie>("Zombie Pigman"),
                typed<EntityGhast>("Ghast"),
                named("Wither Skeleton") { it is EntitySkeleton && it.skeletonType == 1 },
                typed<EntityMagmaCube>("Magma Cube"),
                typed<EntityBlaze>("Blaze"),
                typed<EntityEnderman>("Enderman"),
                typed<EntityDragon>("Ender Dragon"),
                typed<EntityWither>("Wither"),
                typed<EntityEndermite>("Endermite"),
                typed<EntityGiantZombie>("Giant"),
            ),
            typed<IAnimals>("Passive").children(
                typed<EntityPig>("Pig"),
                typed<EntitySheep>("Sheep"),
                typed<EntityCow>("Cow"),
                typed<EntityChicken>("Chicken"),
                typed<EntitySquid>("Squid"),
                typed<EntitySquid>("Squid"),
                typed<EntityWolf>("Wolf"),
                typed<EntityMooshroom>("Mushroom"),
                typed<EntityIronGolem>("Iron Golem"),
                typed<EntitySnowman>("Snowman"),
                typed<EntityOcelot>("Ocelot"),
                typed<EntityHorse>("Horse"),
                typed<EntityRabbit>("Rabbit"),
                typed<EntityVillager>("Villager"),
            ),
        ),
        named("Others") { true }.children(
            typed<IProjectile>("Projectile").children(
                typed<EntityArrow>("Arrow"),
                typed<EntityEgg>("Egg"),
                typed<EntitySnowball>("Snowball"),
                typed<EntityLargeFireball>("Fireball"),
                typed<EntityEnderPearl>("Ender Pearl"),
                typed<EntitySmallFireball>("Fire Charge"),
                typed<EntityEnderEye>("Ender Eye"),
                typed<EntityPotion>("Splash Potion"),
                typed<EntityExpBottle>("XP Bottle"),
                typed<EntityWitherSkull>("Wither Skull"),
            ),
            typed<EntityTNTPrimed>("TNT"),
            typed<EntityItemFrame>("Item Frame"),
            typed<EntityPainting>("Painting"),
            typed<EntityFallingBlock>("Falling Block"),
            typed<EntityFireworkRocket>("Firework"),
            typed<EntityEnderCrystal>("Ender Crystal"),
            typed<EntityBoat>("Boat"),
            typed<EntityMinecart>("Minecart"),
            typed<EntityLightningBolt>("Lightning Bolt"),
        ),
    )
}

const val BRANCH_HEIGHT = 32

private fun named(name: String, check: (Entity) -> Boolean) = NamedHitbox(name, check)
private inline fun <reified T> typed(name: String) = named(name) { it is T }

class NamedHitbox(
    val name: String,
    val checkEntity: (Entity) -> Boolean,
) : HitboxProvider {
    override val savedHitbox: HitboxProfile
        get() = HitboxMainTree.savedMap.computeIfAbsent(name) { HitboxProfile() }

    private val readHitbox: HitboxProfile?
        get() = HitboxMainTree.savedMap[name]

    private var override = name in HitboxMainTree.savedMap
    private var checkbox = ConfigCheckbox(::override.javaField, this, name, "", "", "", 1)

    private var _option: List<BasicOption>? = null
    private var _lastOverride: Boolean = override
    private val options: List<BasicOption>
        get() = _option?.takeIf { _lastOverride == override }
            ?: (readHitbox?.let { hitboxProfile ->
                ConfigUtils.getClassOptions(hitboxProfile).also { basicOptions ->
                    for (option in basicOptions) {
                        option.addDependency("Inherited") { override }
                    }
                }
            } ?: def).also {
                _option = it
                _lastOverride = override
            }

    init {
        checkbox.addListener {
            if (override) HitboxMainTree.savedMap[name] = HitboxProfile()
            else HitboxMainTree.savedMap.remove(name)
        }
    }

    override fun findHitbox(entity: Entity): HitboxProfile? = readHitbox?.takeIf { override && checkEntity(entity) }
    override fun find(index: Int): NamedHitbox? = takeIf { index == 0 }
    override fun size() = 1

    override fun drawBranch(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        checkbox.draw(vg, x + 14, y, inputHandler)
    }

    fun children(vararg children: HitboxProvider) = ParentNamedHitbox(this, listOf(*children))

    override fun drawOption(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        var optionY = y
        for (option in options) {
            option.draw(vg, x, optionY, inputHandler)
            optionY += option.height + 16
        }
    }
}

private val def = ConfigUtils.getClassOptions(HitboxProfile()).also {
    for (option in it) {
        option.addDependency("Inherited from Default Minecraft Settings") { false }
    }
}

class ParentNamedHitbox(
    val hitbox: NamedHitbox,
    private val children: List<HitboxProvider>,
) : HitboxProvider by hitbox {
    private var expanded = true

    private val expandedList: List<HitboxProvider>
        get() = if (expanded) {
            listOf(hitbox) + children
        } else {
            listOf(hitbox)
        }

    override fun size() = expandedList.sumOf { it.size() }

    override fun find(index: Int): NamedHitbox? {
        var indexMut = index
        for (child in expandedList) {
            child.find(indexMut)?.let { return it }
            indexMut -= child.size()
        }
        return null
    }

    override fun drawBranch(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        if (inputHandler.isAreaClicked(x.toFloat(), y.toFloat(), 10f, BRANCH_HEIGHT.toFloat())) {
            expanded = !expanded
        }

        vg.drawText(if (expanded) "v" else ">", x, y + BRANCH_HEIGHT / 2, 0xFFAAAAAA.toInt(), 14, Fonts.SEMIBOLD)
        hitbox.drawBranch(vg, x, y, inputHandler)

        if (!expanded) return
        var y2 = y + hitbox.size() * BRANCH_HEIGHT
        for (child in children) {
            child.drawBranch(vg, x + 10, y2, inputHandler)
            y2 += child.size() * BRANCH_HEIGHT
        }
    }

    override fun findHitbox(entity: Entity): HitboxProfile? =
        children.firstNotNullOfOrNull {
            it.findHitbox(entity)
        } ?: hitbox.findHitbox(entity)
}

interface HitboxProvider {
    val savedHitbox: HitboxProfile
    fun findHitbox(entity: Entity): HitboxProfile?
    fun drawBranch(vg: Long, x: Int, y: Int, inputHandler: InputHandler)
    fun size(): Int
    fun find(index: Int): NamedHitbox?
    fun drawOption(vg: Long, x: Int, y: Int, inputHandler: InputHandler)
}

inline fun <T, R : Any> List<T>.lastNotNullOfOrNull(transform: (T) -> R?): R? {
    val iterator = listIterator(size)
    while (iterator.hasPrevious()) {
        return transform(iterator.previous()) ?: continue
    }
    return null
}

