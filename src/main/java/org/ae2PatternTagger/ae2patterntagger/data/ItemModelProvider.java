package org.ae2PatternTagger.ae2patterntagger.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;
import org.ae2PatternTagger.ae2patterntagger.items.ItemRegisters;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Ae2patterntagger.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ItemRegisters.TAGGER.get());
    }
}
