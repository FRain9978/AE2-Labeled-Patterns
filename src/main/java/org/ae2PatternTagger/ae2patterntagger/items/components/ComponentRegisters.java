package org.ae2PatternTagger.ae2patterntagger.items.components;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ae2PatternTagger.ae2patterntagger.Ae2patterntagger;

public class ComponentRegisters {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Ae2patterntagger.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PatternProviderTag>> PATTERN_PROVIDER_TAG =
            COMPONENTS.registerComponentType(
                    "pattern_provider_tag",
                    builder -> builder
                            .persistent(PatternProviderTag.CODEC)
                            .networkSynchronized(PatternProviderTag.STREAM_CODEC)
                            .cacheEncoding()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TaggerSetting>> TAGGER_SETTING =
            COMPONENTS.registerComponentType(
                    "tagger_setting",
                    builder -> builder
                            .persistent(TaggerSetting.CODEC)
                            .networkSynchronized(TaggerSetting.STREAM_CODEC)
                            .cacheEncoding()
            );
}
