package org.ae2LabeledPatterns.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Ae2LabeledPatterns.MODID)
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
    }
}
