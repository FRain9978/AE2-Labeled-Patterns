package org.ae2PatternTagger.parts;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2PatternTagger.Ae2patterntagger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.ae2PatternTagger.Ae2patterntagger.MODID;

public class PartRegisters {
    private static final Set<ResourceLocation> models = new HashSet();
    public static final DeferredRegister.Items PARTS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> ADVANCED_PATTERN_ACCESS_TERMINAL =
            registerPart("advanced_pattern_access_terminal", AdvancedPatternAccessTerminalPart.class, AdvancedPatternAccessTerminalPart::new);

    private static <T extends IPart> DeferredItem<Item> registerPart(String name, Class<T> partClass, Function<IPartItem<T>, T> factory){
        models.addAll(PartModelsHelper.createModels(partClass));
        return PARTS.registerItem(name, (props) -> new PartItem<>(props, partClass, factory));
    }

    public static void registerModels(ModelEvent.RegisterAdditional event) {
        models.stream().map(ModelResourceLocation::standalone).forEach(event::register);
    }

    private static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(Ae2patterntagger.MODID, id);
    }
}
