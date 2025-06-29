package org.ae2PatternTagger.items;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ae2PatternTagger.Ae2patterntagger.MODID;

public class ItemRegisters {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final String MY_ITEM_ID = "my_item";
    public static final DeferredItem<Item> MY_ITEM = ITEMS.registerSimpleItem(MY_ITEM_ID, new Item.Properties());

    public static final String TAGGER_ID = "tagger";
    public static final DeferredItem<Item> TAGGER = ITEMS.registerItem(
            TAGGER_ID,
            TaggerItem::new,
//            new Item.Properties().component(ComponentRegisters.PATTERN_PROVIDER_TAG, new PatternProviderTag("sample"))
            new Item.Properties());
}
