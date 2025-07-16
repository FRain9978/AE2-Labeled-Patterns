package org.ae2LabeledPatterns.integration;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import org.ae2LabeledPatterns.attachments.AttachmentRegisters;
import org.ae2LabeledPatterns.items.components.PatternProviderLabel;
import org.jetbrains.annotations.NotNull;

public class CheckProvider {
    public static boolean isEntityProvider(Object object) {
        try {
            return (object instanceof BlockEntity blockEntity) &&
                    (isBasicProvider(blockEntity) != null ||
                            isAAEProvider(blockEntity) != null ||
                            isEAEAssemblerMatrixProvider(blockEntity) != null
                    );
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static @NotNull PatternProviderLabel getEntityProviderLabel(Object object) {
        if (isEntityProvider(object)){
            return ((BlockEntity)object).getData(AttachmentRegisters.PATTERN_PROVIDER_LABEL);
        }
        return PatternProviderLabel.Empty;
    }

    private static PatternProviderLogicHost isBasicProvider(BlockEntity blockEntity) {
        try {
            return blockEntity instanceof PatternProviderLogicHost patternProviderLogicHost ? patternProviderLogicHost : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static AdvPatternProviderLogicHost isAAEProvider(BlockEntity blockEntity) {
        try {
            return blockEntity instanceof AdvPatternProviderLogicHost advPatternProviderLogicHost? advPatternProviderLogicHost : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static TileAssemblerMatrixPattern isEAEAssemblerMatrixProvider(BlockEntity blockEntity) {
        try {
            return blockEntity instanceof TileAssemblerMatrixPattern tileAssemblerMatrixPattern ? tileAssemblerMatrixPattern : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

}
