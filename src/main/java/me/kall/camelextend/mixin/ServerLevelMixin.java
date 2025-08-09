package me.kall.camelextend.mixin;

import me.kall.camelextend.api.CamelEdible;
import me.kall.camelextend.data.CamelEdibleData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Unique
    private CamelEdibleData camelExtend$camelEdibleData;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.camelExtend$camelEdibleData = CamelEdibleData.get((ServerLevel) (Object) this);
    }

    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    private void camelExtend$getCamelEdible(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        boolean wasCamelEdible = ((CamelEdible)oldState.getBlock()).camelExtend$isCamelEdible();
        boolean isCamelEdible = ((CamelEdible)newState.getBlock()).camelExtend$isCamelEdible();

        if (this.camelExtend$camelEdibleData == null) this.camelExtend$camelEdibleData = CamelEdibleData.get((ServerLevel) (Object) this);

        if (wasCamelEdible) {
            this.camelExtend$camelEdibleData.removeCamelEdible((ServerLevel) (Object) this, pos);
        }

        if (isCamelEdible) {
            this.camelExtend$camelEdibleData.addCamelEdible((ServerLevel) (Object) this, pos);
        }
    }
}