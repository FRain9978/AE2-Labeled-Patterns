package org.ae2LabeledPatterns.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ae2LabeledPatterns.AE2LabeledPatterns;
import org.ae2LabeledPatterns.integration.ae2wtlib.AE2wtlibRegisters;
import org.ae2LabeledPatterns.items.ItemRegisters;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AE2LabeledPatterns.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        basicItem(ItemRegisters.LABELER.get());
        basicItem(ItemRegisters.WIRELESS_TERMINAL.get());
    }
}
