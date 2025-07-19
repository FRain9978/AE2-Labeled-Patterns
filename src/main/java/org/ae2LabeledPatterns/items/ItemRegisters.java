package org.ae2LabeledPatterns.items;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.integration.ae2wtlib.WirelessLabeledTerminalItem;

import static org.ae2LabeledPatterns.AE2LabeledPatterns.MODID;

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
//    public static final WirelessLabeledTerminalItem WIRELESS_TERMINAL = new WirelessLabeledTerminalItem();
    public static final DeferredItem<Item> WIRELESS_TERMINAL = ITEMS.registerItem(
            "wireless_labeled_pattern_access_terminal",
            WirelessLabeledTerminalItem::new,
            new Item.Properties());
}
