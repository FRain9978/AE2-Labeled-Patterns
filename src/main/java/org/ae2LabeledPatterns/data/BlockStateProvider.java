package org.ae2LabeledPatterns.data;

import appeng.core.AppEng;
import appeng.datagen.providers.models.AE2BlockStateProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.ae2LabeledPatterns.Ae2LabeledPatterns;
import org.ae2LabeledPatterns.blocks.BlockRegisters;
import org.ae2LabeledPatterns.parts.PartRegisters;

public class BlockStateProvider extends AE2BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Ae2LabeledPatterns.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BlockRegisters.MY_BLOCK);

        registerTerminalPartModel(
                PartRegisters.LABELED_PATTERN_ACCESS_TERMINAL.getId(),
                "pattern_access_terminal"
                );
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }


    private void registerTerminalPartModel(ResourceLocation id, String parentTerminal) {
        var path = id.getPath();
        var partName = path.substring(0, path.lastIndexOf('_'));
        var lightsBright = Ae2LabeledPatterns.makeId("part/" + partName + "_terminal_bright");
        var lightsMedium = Ae2LabeledPatterns.makeId("part/" + partName + "_terminal_medium");
        var lightsDark = Ae2LabeledPatterns.makeId("part/" + partName + "_terminal_dark");

        var base_on = AppEng.makeId("part/" + parentTerminal + "_on");
        var base_off = AppEng.makeId("part/" + parentTerminal + "_off");
        var itemBase = AppEng.makeId("item/" + parentTerminal);

        models().withExistingParent("part/" + path + "_on", base_on)
                .texture("lightsBright", lightsBright)
                .texture("lightsMedium", lightsMedium)
                .texture("lightsDark", lightsDark);
        models().withExistingParent("part/" + path + "_off", base_off)
                .texture("lightsBright", lightsBright)
                .texture("lightsMedium", lightsMedium)
                .texture("lightsDark", lightsDark);

        itemModels().withExistingParent("item/" + path, itemBase)
                .texture("front_bright", lightsBright)
                .texture("front_medium", lightsMedium)
                .texture("front_dark", lightsDark);
    }
}
