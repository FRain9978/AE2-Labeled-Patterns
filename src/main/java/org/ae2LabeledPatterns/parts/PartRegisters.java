package org.ae2LabeledPatterns.parts;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.client.color.item.ItemColor;
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

    public static void registerColor(RegisterColorHandlersEvent.Item event){
        for (var part : parts) {
            var item = part.asItem();
            if (item instanceof PartItem) {
                AEColor color = AEColor.TRANSPARENT;
                if (item instanceof ColoredPartItem) {
                    color = ((ColoredPartItem<?>) item).getColor();
                }
                event.register(makeOpaque(new StaticItemColor(color)), item);
            }
        }
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }
}
