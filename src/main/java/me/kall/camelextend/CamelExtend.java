package me.kall.camelextend;

import me.kall.camelextend.api.Cactus;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(CamelExtend.MOD_ID)
public final class CamelExtend {
    public static final String MOD_ID = "camelextend";
    public static final String MOD_NAME = "CamelExtend";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public CamelExtend(@NotNull FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> ForgeRegistries.BLOCKS.forEach(block -> {
            if (block instanceof CactusBlock) ((Cactus)block).camelExtend$setIsCactus(true);
        })));
    }
}
