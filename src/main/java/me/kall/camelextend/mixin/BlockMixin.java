package me.kall.camelextend.mixin;

import me.kall.camelextend.api.Cactus;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public class BlockMixin implements Cactus {
    @Unique private boolean camelExtend$isCactus;

    @Override
    public boolean camelExtend$isCactus() {
        return this.camelExtend$isCactus;
    }

    @Override
    public void camelExtend$setIsCactus(boolean isCactus) {
        this.camelExtend$isCactus = isCactus;
    }
}
