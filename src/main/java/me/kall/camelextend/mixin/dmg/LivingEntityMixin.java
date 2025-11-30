package me.kall.camelextend.mixin.dmg;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    protected void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {}
}
