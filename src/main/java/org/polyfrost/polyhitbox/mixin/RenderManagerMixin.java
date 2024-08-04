package org.polyfrost.polyhitbox.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.polyfrost.polyhitbox.hooks.EntityHook;
import org.polyfrost.polyhitbox.config.ModConfig;
import org.polyfrost.polyhitbox.hooks.MixinHooksKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.ArrayList;

@Mixin(RenderManager.class)
public abstract class RenderManagerMixin {

    @Inject(method = "renderDebugBoundingBox", at = @At("HEAD"), cancellable = true)
    public void polyHitbox$injectHitbox(Entity entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo callbackInfo) {
        if (MixinHooksKt.overrideHitbox(entity, x, y, z, partialTicks)) {
            callbackInfo.cancel();
        }
    }

    @Redirect(method = "doRenderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z"))
    public boolean ESP(Entity entity){
        if(isBot(entity)){
            return entity.isInvisible();
        } else{
            return !((EntityHook) entity).polyHitbox$getHitboxConfig().getESP() && entity.isInvisible();
        }
    }

    public boolean isBot(Entity entity){
        if (entity instanceof EntityPlayer && (((EntityPlayer) entity).getDisplayNameString().contains("§c") || ((EntityPlayer) entity).getDisplayNameString().contains("[NPC]") || ((EntityPlayer) entity).getDisplayNameString().contains("[BOT]") || ((EntityPlayer) entity).getDisplayNameString().contains("iAT3") || ((EntityPlayer) entity).getDisplayNameString().isEmpty() || (entity.getUniqueID().version() == 2) || (((EntityPlayer) entity).getDisplayNameString().contains("§") && (((EntityPlayer) entity).getDisplayNameString().contains("SHOP") || ((EntityPlayer) entity).getDisplayNameString().contains("UPGRADE"))))) {
            return true;
        } else {
            for (String name : getAllPlayerNamesFromTabList()) {
                if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getDisplayNameString().contains(name)) {
                    return false;
                }
            }
            return true;
        }
    }

    public ArrayList<String> getAllPlayerNamesFromTabList() {
        ArrayList<String> playerNames = new ArrayList<>();
        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();

        if (netHandler != null) {
            for (NetworkPlayerInfo info : netHandler.getPlayerInfoMap()) {
                playerNames.add(info.getGameProfile().getName());
            }
        }

        return playerNames;
    }

    @Redirect(method = "doRenderEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;debugBoundingBox:Z"))
    private boolean redirectBoundingBox(RenderManager instance) {
        return ModConfig.INSTANCE.enabled || instance.isDebugBoundingBox();
    }
}
