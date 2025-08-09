package org.ae2LabeledPatterns.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.ae2LabeledPatterns.AE2LabeledPatterns;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
//@EventBusSubscriber(modid = AE2LabeledPatterns.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

//    private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ModConfigSpec.IntValue MAX_ALLOW_BLOCK_SPACE = BUILDER.comment("Max allow block space for labeler").defineInRange("maxAllowBlockSpace", 128, 0, Integer.MAX_VALUE);

//    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
//    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    public static final ModConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
    public static int maxAllowBlockSpace(){
        return MAX_ALLOW_BLOCK_SPACE.get();
    };
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;

//    @SubscribeEvent
//    static void onLoad(final ModConfigEvent event) {
//        maxAllowBlockSpace = MAX_ALLOW_BLOCK_SPACE.get();
//    }
}
