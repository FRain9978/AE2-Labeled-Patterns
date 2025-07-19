package org.ae2LabeledPatterns.tags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.ae2LabeledPatterns.AE2LabeledPatterns;

public class ModTags {
    public static class Items {
        // like ae2:metal_ingots
        public static final TagKey<Item> METAL_INGOT_BLOCK = createTag("metal_ingot_blocks");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(AE2LabeledPatterns.makeId(name));
        }
    }

    public static class Blocks{
        // like ae2:metal_ingots
        public static final TagKey<Block> METAL_INGOT_BLOCK = createTag("metal_ingot_blocks");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(AE2LabeledPatterns.makeId(name));
        }
    }
}
