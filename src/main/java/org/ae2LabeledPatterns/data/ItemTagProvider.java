package org.ae2LabeledPatterns.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.tags.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Ae2LabeledPatterns.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(ModTags.Blocks.METAL_INGOT_BLOCK, ModTags.Items.METAL_INGOT_BLOCK);
    }
}
