package org.ae2LabeledPatterns.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.tags.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AE2LabeledPatterns.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.METAL_INGOT_BLOCK)
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/copper"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/tin"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/iron"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/gold"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/brass"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/nickel"))
                .addOptionalTag(ResourceLocation.parse("c:storage_blocks/aluminium"))
        ;
    }
}
