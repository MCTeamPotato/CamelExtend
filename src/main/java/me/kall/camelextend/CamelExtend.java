package me.kall.camelextend;

import com.google.common.collect.Lists;
import me.kall.camelextend.api.CamelEdible;
import me.kall.camelextend.api.JsonConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Mod(CamelExtend.MOD_ID)
public final class CamelExtend {
    public static final String MOD_ID = "camelextend";

    public static final JsonConfig CONFIG = JsonConfig.create(MOD_ID, "1.0.0").put("CamelEdible", Lists.newArrayList("minecraft:cactus")).initialize();
    private static final Set<String> CAMEL_EDIBLE = CONFIG.getSet("CamelEdible", String.class);

    public CamelExtend(@NotNull FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> ForgeRegistries.BLOCKS.getEntries().forEach(entry -> ((CamelEdible)entry.getValue()).camelExtend$setIsCamelEdible(CAMEL_EDIBLE.contains(entry.getKey().location().toString())))));
    }
}
