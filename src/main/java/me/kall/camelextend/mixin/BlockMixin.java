package me.kall.camelextend.mixin;

import me.kall.camelextend.api.CamelEdible;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public class BlockMixin implements CamelEdible {
    @Unique private boolean camelExtend$isCamelEdible;

    @Override
    public boolean camelExtend$isCamelEdible() {
        return this.camelExtend$isCamelEdible;
    }

    @Override
    public void camelExtend$setIsCamelEdible(boolean isCactus) {
        this.camelExtend$isCamelEdible = isCactus;
    }
}
