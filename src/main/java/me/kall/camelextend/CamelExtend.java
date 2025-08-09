package me.kall.camelextend;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import me.kall.camelextend.api.CamelEdible;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod(CamelExtend.MOD_ID)
public final class CamelExtend {
    public static final String MOD_ID = "camelextend";
    public static final String MOD_NAME = "CamelExtend";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public CamelExtend(@NotNull FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> ForgeRegistries.BLOCKS.getEntries().forEach(entry -> ((CamelEdible)entry.getValue()).camelExtend$setIsCamelEdible(CAMEL_EDIBLE.get().contains(entry.getKey().location().toString())))));
    }

    public static final ForgeConfigSpec CONFIG;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CAMEL_EDIBLE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push(MOD_NAME);
        CAMEL_EDIBLE = builder.defineList("CamelEdible", Lists.newArrayList("minecraft:cactus"), Predicates.alwaysTrue());
        builder.pop();
        CONFIG = builder.build();
    }
}
