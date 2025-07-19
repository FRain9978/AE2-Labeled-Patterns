package org.ae2LabeledPatterns.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2LabeledPatterns.AE2LabeledPatterns;

import java.util.List;

public class ComponentRegisters {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AE2LabeledPatterns.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PatternProviderLabel>> PATTERN_PROVIDER_LABEL =
            COMPONENTS.registerComponentType(
                    "pattern_provider_label",
                    builder -> builder
                            .persistent(PatternProviderLabel.CODEC)
                            .networkSynchronized(PatternProviderLabel.STREAM_CODEC)
                            .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<PatternProviderLabel>>> SAVED_LABELS =
            COMPONENTS.registerComponentType(
                    "saved_labels",
                    builder -> builder
                            .persistent(Codec.list(PatternProviderLabel.CODEC))
                            .networkSynchronized(PatternProviderLabel.STREAM_CODEC.apply(ByteBufCodecs.list()))
                            .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LabelerSetting>> LABELER_SETTING =
            COMPONENTS.registerComponentType(
                    "labeler_setting",
                    builder -> builder
                            .persistent(LabelerSetting.CODEC)
                            .networkSynchronized(LabelerSetting.STREAM_CODEC)
                            .cacheEncoding()
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MultiBlockTarget>> MULTI_BLOCK_TARGET =
            COMPONENTS.registerComponentType(
                    "multi_block_target",
                    builder -> builder
                            .persistent(MultiBlockTarget.CODEC)
                            .networkSynchronized(MultiBlockTarget.STREAM_CODEC)
                            .cacheEncoding()
            );
}
