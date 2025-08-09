package me.kall.camelextend.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.animal.camel.Camel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camel.class)
public class CamelMixin extends LivingEntityMixin {
    @Override
    protected void onHurt(@NotNull DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.CACTUS)) cir.setReturnValue(false);
    }
}
