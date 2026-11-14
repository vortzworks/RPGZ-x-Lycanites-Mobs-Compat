package com.vortz.rpgzlycanites.mixin;

import com.vortz.rpgzlycanites.RpgzBridge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lycanites creatures spawn their loot through their own dropItem() -> spawnAtLocation(stack, float)
 * path, which skips the vanilla hook RpgZ relies on. While the creature is dying, send its
 * drops into RpgZ's corpse inventory instead.
 */
@Mixin(targets = "com.lycanitesmobs.core.entity.base.BaseCreatureEntity", remap = false)
public abstract class BaseCreatureEntityMixin {

    @Inject(method = "dropItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void rpgzLycanites$captureDrop(ItemStack stack, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide || !self.isDeadOrDying()) {
            return;
        }
        if (RpgzBridge.tryCapture(self, stack)) {
            ci.cancel();
        }
    }
}
