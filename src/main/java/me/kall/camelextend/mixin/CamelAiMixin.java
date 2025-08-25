package me.kall.camelextend.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import me.kall.camelextend.behavior.EatEdibleBlockBehavior;
import net.minecraft.world.entity.animal.camel.CamelAi;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CamelAi.class)
public abstract class CamelAiMixin {
    @SuppressWarnings("unchecked")
    @Redirect(method = "initIdleActivity", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;", remap = false))
    private static <E> @NotNull ImmutableList<E> init(E e1, E e2, E e3, E e4, E e5, E e6) {
        return ImmutableList.of((E) Pair.of(0, new EatEdibleBlockBehavior()), e1, e2, e3, e4, e5, e6);
    }
}
