package org.ae2LabeledPatterns.items;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ae2LabeledPatterns.Ae2LabeledPatterns.MODID;

public class ItemRegisters {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final String LABELER_ID = "labeler";
    public static final DeferredItem<Item> LABELER = ITEMS.registerItem(
            LABELER_ID,
            LabelerItem::new,
//            new Item.Properties().component(ComponentRegisters.PATTERN_PROVIDER_LABEL, new PatternProviderLabel("sample"))
            new Item.Properties()
//                    .component(ComponentRegisters.LABELER_SETTING.value(), new LabelerSetting())
//                    .component(ComponentRegisters.MULTI_BLOCK_TARGET.value(), new MultiBlockTarget())
    );
}
