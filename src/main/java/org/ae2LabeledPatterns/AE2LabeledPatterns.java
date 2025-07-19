package org.ae2LabeledPatterns;

import appeng.api.ids.AECreativeTabIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.capability.CapabilityRegisters;
import org.ae2LabeledPatterns.config.Config;
import org.ae2LabeledPatterns.integration.ae2wtlib.AE2wtlibRegisters;
import org.ae2LabeledPatterns.items.ItemRegisters;
import org.ae2LabeledPatterns.items.components.ComponentRegisters;
import org.ae2LabeledPatterns.menus.MenuRegisters;
import org.ae2LabeledPatterns.network.InitNetwork;
import org.ae2LabeledPatterns.parts.PartRegisters;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AE2LabeledPatterns.MODID)
public class AE2LabeledPatterns {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "ae2labeledpatterns";
    // Directly reference a slf4j logger
//    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "ae2patterntagger" namespace
//    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "ae2patterntagger" namespace
//    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "ae2patterntagger" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "ae2patterntagger:example_block", combining the namespace and path
//    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).destroyTime(1f).lightLevel(blockState -> 15));
    // Creates a new BlockItem with the id "ae2patterntagger:example_block", combining the namespace and path
//    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a creative tab with the id "ae2patterntagger:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("ae2_labeled_patterns", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.ae2labeledpatterns.main"))
                    .withTabsBefore(AECreativeTabIds.FACADES).icon(() -> ItemRegisters.LABELER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ItemRegisters.LABELER.get());
                        output.accept(PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL.get());
    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AE2LabeledPatterns(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
//        modEventBus.addListener(this::commonSetup);

        ComponentRegisters.COMPONENTS.register(modEventBus);
        AttachmentRegisters.ATTACHMENTS.register(modEventBus);

        ItemRegisters.ITEMS.register(modEventBus);
        if (ModList.get().isLoaded("ae2wtlib")|| LoadingModList.get().getMods()
                .stream().map(ModInfo::getModId)
                .anyMatch("ae2wtlib"::equals)) {
            modEventBus.addListener(AE2wtlibRegisters::Init);
        }
        modEventBus.addListener(CapabilityRegisters::Init);
        PartRegisters.PARTS.register(modEventBus);

        modEventBus.addListener(InitNetwork::init);
        MenuRegisters.MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

}
