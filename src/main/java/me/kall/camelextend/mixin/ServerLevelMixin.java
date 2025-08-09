package me.kall.camelextend.mixin;

import me.kall.camelextend.api.Cactus;
import me.kall.camelextend.data.CactusData;
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
    private CactusData camelExtend$cactusData;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.camelExtend$cactusData = CactusData.get((ServerLevel) (Object) this);
    }

    @Inject(method = "onBlockStateChange", at = @At("HEAD"))
    private void camelExtend$getCactus(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        boolean wasCactus = ((Cactus)oldState.getBlock()).camelExtend$isCactus();
        boolean isCactus = ((Cactus)newState.getBlock()).camelExtend$isCactus();

        if (this.camelExtend$cactusData == null) this.camelExtend$cactusData = CactusData.get((ServerLevel) (Object) this);

        if (wasCactus) {
            this.camelExtend$cactusData.removeCactus((ServerLevel) (Object) this, pos);
        }

        if (isCactus) {
            this.camelExtend$cactusData.addCactus((ServerLevel) (Object) this, pos);
        }
    }
}