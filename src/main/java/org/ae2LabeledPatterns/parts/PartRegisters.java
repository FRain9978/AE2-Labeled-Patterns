package org.ae2LabeledPatterns.parts;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.util.AEColor;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.ae2LabeledPatterns.Ae2LabeledPatterns.MODID;

public class PartRegisters {
    private static final Set<ResourceLocation> models = new HashSet();
    private static final Set<DeferredItem<Item>> parts = new HashSet<>();
    public static final DeferredRegister.Items PARTS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> LABELED_PATTERN_ACCESS_TERMINAL =
            registerPart("labeled_pattern_access_terminal", LabeledPatternAccessTerminalPart.class, LabeledPatternAccessTerminalPart::new);

    private static <T extends IPart> DeferredItem<Item> registerPart(String name, Class<T> partClass, Function<IPartItem<T>, T> factory){
//        models.addAll(PartModelsHelper.createModels(partClass));
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        DeferredItem<Item> partItem = PARTS.registerItem(name, (props) -> new PartItem<>(props, partClass, factory));
        parts.add(partItem);
        return partItem;
    }

    public static void registerModels(ModelEvent.RegisterAdditional event) {
        models.stream().map(ModelResourceLocation::standalone).forEach(event::register);
    }

    public static void registerModels(){
//        PartModels.registerModels(models);
    }

    private static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(Ae2LabeledPatterns.MODID, id);
    }
}
