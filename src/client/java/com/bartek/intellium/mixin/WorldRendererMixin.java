package com.bartek.intellium.mixin;

import com.bartek.intellium.IntelliumCuller;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class WorldRendererMixin {
    @Inject(
            method = "shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends Entity> void intellium$shouldRender(
            E entity,
            Frustum frustum,
            double x,
            double y,
            double z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        double camX = entity.getX() - x;
        double camY = entity.getY() - y;
        double camZ = entity.getZ() - z;

        if (!IntelliumCuller.shouldRenderEntity(entity, frustum, camX, camY, camZ)) {
            cir.setReturnValue(false);
        }
    }
}
