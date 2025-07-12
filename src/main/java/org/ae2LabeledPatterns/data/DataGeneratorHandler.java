package org.ae2LabeledPatterns.data;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Ae2LabeledPatterns.MODID, value = Dist.CLIENT)
public class DataGeneratorHandler{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var helper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new BlockStateProvider(output, helper));
        generator.addProvider(event.includeClient(), new ItemModelProvider(output, helper));
        generator.addProvider(
                event.includeClient(),
                new LanguageProviders.ChineseLanguageProvider(output)
        );
        generator.addProvider(
                event.includeClient(),
                new LanguageProviders.EnglishLanguageProvider(output)
        );
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));
        var blockTagsProvider = new BlockTagProvider(output, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), helper));
    }
}
