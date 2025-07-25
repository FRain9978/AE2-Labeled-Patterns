package org.ae2LabeledPatterns.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.integration.ae2wtlib.AE2wtlibRegisters;
import org.ae2LabeledPatterns.items.ItemRegisters;
import org.ae2LabeledPatterns.parts.PartRegisters;
import org.ae2LabeledPatterns.tags.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        super.buildRecipes(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegisters.LABELER)
                .requires(ConventionTags.ILLUMINATED_PANEL)
                .requires(ModTags.Items.METAL_INGOT_BLOCK)
                .requires(ConventionTags.QUARTZ_KNIFE)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy("has_quartz_knife", has(ConventionTags.QUARTZ_KNIFE))
                .unlockedBy("has_engineering_processor", has(AEItems.ENGINEERING_PROCESSOR))
                .save(recipeOutput, AE2LabeledPatterns.makeId("labeler"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL)
                .requires(ItemRegisters.LABELER)
                .requires(AEParts.PATTERN_ACCESS_TERMINAL)
                .unlockedBy("has_labeler", has(ItemRegisters.LABELER))
                .save(recipeOutput, AE2LabeledPatterns.makeId("labeled_pattern_access_terminal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegisters.WIRELESS_TERMINAL)
                .pattern("A")
                .pattern("B")
                .pattern("C")
                .define('A', AEItems.WIRELESS_RECEIVER)
                .define('B', PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL)
                .define('C', AEBlocks.DENSE_ENERGY_CELL)
                .unlockedBy("has_labeled_pattern_access_terminal", has(PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL))
                .save(recipeOutput, AE2LabeledPatterns.makeId("wireless_labeled_pattern_access_terminal"))
        ;
    }
}
